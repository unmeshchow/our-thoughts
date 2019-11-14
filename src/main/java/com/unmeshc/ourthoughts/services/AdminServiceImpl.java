package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.*;
import com.unmeshc.ourthoughts.mappers.CommentMapper;
import com.unmeshc.ourthoughts.mappers.PostMapper;
import com.unmeshc.ourthoughts.mappers.UserMapper;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import com.unmeshc.ourthoughts.services.pagination.AdminCommentPageTracker;
import com.unmeshc.ourthoughts.services.pagination.AdminPostPageTracker;
import com.unmeshc.ourthoughts.services.pagination.AdminUserPageTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by uc on 10/26/2019
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    private static final int PAGE_SIZE = 10;

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;
    private final AdminPostPageTracker adminPostPageTracker;
    private final AdminCommentPageTracker adminCommentPageTracker;
    private final AdminUserPageTracker adminUserPageTracker;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public AdminServiceImpl(RoleRepository roleRepository,
                            UserService userService,
                            PostService postService,
                            CommentService commentService,
                            PasswordEncoder passwordEncoder,
                            AdminPostPageTracker adminPostPageTracker,
                            AdminCommentPageTracker adminCommentPageTracker,
                            AdminUserPageTracker adminUserPageTracker,
                            UserMapper userMapper,
                            PostMapper postMapper,
                            CommentMapper commentMapper) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.passwordEncoder = passwordEncoder;
        this.adminPostPageTracker = adminPostPageTracker;
        this.adminCommentPageTracker = adminCommentPageTracker;
        this.adminUserPageTracker = adminUserPageTracker;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public void createAdminUser() {
        Role admin = roleRepository.findByName("ADMIN").orElse(null);
        if (admin == null) {
            log.error("Admin role not found");
            throw new RuntimeException("Admin role not found, try again.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(admin);

        User adminUser = User.builder().email(ADMIN_EMAIL).active(true).roles(roles).
                password(passwordEncoder.encode(ADMIN_PASSWORD)).build();

        userService.saveOrUpdateUser(adminUser);
    }

    @Override
    public boolean isAdminExists() {
        return userService.isEmailExists(ADMIN_EMAIL);
    }

    @Override
    public UserAdminListDto getAllUsers(int page, int size, boolean delete) {

        int currentPage = page > 0 ? page : adminUserPageTracker.getCurrentPage();
        int pageSize = size > 0 ? size : PAGE_SIZE;

        // zero based page number
        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("lastName").ascending().and(Sort.by("firstName").descending()));

        // get the user page
        Page<User> userPage =
                userService.getAllUsersExceptAdminAndInactive(ADMIN_EMAIL, pageable);

        // Fix the last page problem since the last page can be deleted up -
        // retrieve the previous page if any
        if (delete && userPage.getContent().isEmpty() && currentPage > 1) {
            currentPage -= 1;

            // zero based page number
            pageable = PageRequest.of((currentPage - 1), pageSize,
                    Sort.by("lastName").ascending().and(Sort.by("firstName").descending()));
            userPage = userService.getAllUsersExceptAdminAndInactive(ADMIN_EMAIL, pageable);
        }

        // set the current page in the tracker - 1 based
        adminUserPageTracker.setCurrentPage(userPage.getNumber() + 1);

        // convert user page to user admin dto list
        List<UserAdminDto> userAdminDtos = userPage
                .stream()
                .map(userMapper::userToUserAdminDto)
                .collect(Collectors.toList());

        // create and return user admin list dto
        return UserAdminListDto.builder()
                .currentPage(adminUserPageTracker.getCurrentPage())
                .pageNumbers(adminUserPageTracker.getPageNumbersForPagination(userPage))
                .userAdminDtos(userAdminDtos).build();
    }

    @Override
    public UserPostAdminDto getPostsForUser(long userId, int page, int size, boolean delete) {
        User user = userService.getUserById(userId);

        if (adminPostPageTracker.getUserId() != userId) {
            adminPostPageTracker.reset();
        }

        int currentPage = page > 0 ? page : adminPostPageTracker.getCurrentPage();
        int pageSize = size > 0 ? size : PAGE_SIZE;

        // zero based page number
        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("creationDateTime").descending());

        // get the post page for the user
        Page<Post> postPage = postService.getPostsByUser(user, pageable);

        // Fix the last page problem since the last page can be deleted up -
        // retrieve the previous page if any
        if (delete && postPage.getContent().isEmpty() && currentPage > 1) {
            currentPage -= 1;

            // zero based page number
            pageable = PageRequest.of((currentPage - 1), pageSize,
                    Sort.by("creationDateTime").descending());
            postPage = postService.getPostsByUser(user, pageable);
        }

        // set current page and user id in the tracker - current page is 1 based
        adminPostPageTracker.setCurrentPage(postPage.getNumber() + 1);
        adminPostPageTracker.setUserId(userId);

        // convert post page to post admin dto list
        List<PostAdminDto> postAdminDtos = postPage
                .stream()
                .map(postMapper::postToPostAdminDto)
                .collect(Collectors.toList());

        // create and return user post admin dto
        return UserPostAdminDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .postAdminDtos(postAdminDtos)
                .currentPage(adminPostPageTracker.getCurrentPage())
                .pageNumbers(adminPostPageTracker.getPageNumbersForPagination(postPage))
                .build();

    }

    @Override
    public PostCommentAdminDto getCommentsForPost(long postId, int page, int size, boolean delete) {
        Post post = postService.getPostById(postId);

        if (adminCommentPageTracker.getPostId() != postId) {
            adminCommentPageTracker.reset();
        }

        int currentPage = page > 0 ? page : adminCommentPageTracker.getCurrentPage();
        int pageSize = size > 0 ? size : PAGE_SIZE;

        // zero based page number
        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("addingDateTime").descending());

        // get comment page for the post
        Page<Comment> commentPage = commentService.getCommentsByPost(post, pageable);

        // Fix the last page problem since the last page can be deleted up -
        // retrieve the previous page if any
        if (delete && commentPage.getContent().isEmpty() && currentPage > 1) {
            currentPage -= 1;

            // zero based page number
            pageable = PageRequest.of((currentPage - 1), pageSize,
                    Sort.by("addingDateTime").descending());
            commentPage = commentService.getCommentsByPost(post, pageable);
        }

        // set the current page and post in the tracker - current page is 1 based
        adminCommentPageTracker.setCurrentPage(commentPage.getNumber() + 1);
        adminCommentPageTracker.setPostId(postId);

        // convert comment post into comment admin dto
        List<CommentAdminDto> commentAdminDtos = commentPage
                .stream()
                .map(commentMapper::commentToCommentAdminDto)
                .collect(Collectors.toList());

        // create and return post comment admin dto
        return PostCommentAdminDto.builder()
                .id(postId)
                .title(post.getTitle())
                .userId(post.getUser().getId())
                .commentAdminDtos(commentAdminDtos)
                .currentPage(adminCommentPageTracker.getCurrentPage())
                .pageNumbers(adminCommentPageTracker.getPageNumbersForPagination(commentPage))
                .build();
    }

    @Override
    public void deleteCommentById(long commentId) {
        commentService.deleteCommentById(commentId);
    }

    @Override
    @Transactional
    public void deletePostWithCommentsById(long postId) {
        // get the post
        Post post = postService.getPostById(postId);

        // deleteUser all comments for the post
        commentService.deleteCommentsByPost(post);

        //deleteUser the post
        postService.deletePost(post);
    }

    @Override
    @Transactional
    public void deleteUserWithPostsById(long userId) {
        // get the user
        User user = userService.getUserById(userId);

        // get posts for the user and deleteUser them all
        postService.getPostsByUser(user).forEach(
                post -> deletePostWithCommentsById(post.getId()));

        // deleteUser the user
        userService.deleteUser(user);
    }

    @Override
    public void changeAdminPasswordAndLogout(String newPassword, HttpServletRequest request) {
        changeAdminPassword(newPassword);
        try {
            request.logout();
        } catch (Exception exception) {
            log.debug("Error occurred during logout");
        }
    }

    @Override
    public void resetAdminPassword() {
        changeAdminPassword(ADMIN_PASSWORD);
    }

    private void changeAdminPassword(String newPassword) {
        User user = userService.getUserByEmail(ADMIN_EMAIL);
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveOrUpdateUser(user);
    }
}