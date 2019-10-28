package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by uc on 10/26/2019
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostService postService;
    private final CommentService commentService;

    public AdminServiceImpl(UserService userService,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder,
                            PostService postService,
                            CommentService commentService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
        this.commentService = commentService;
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
    public Page<User> getAllUsers(Pageable pageable) {

        return userService.getAllUsers(ADMIN_EMAIL, pageable);
    }

    @Override
    public Page<Post> getPostForUser(User user, Pageable pageable) {
        return postService.getPostForUser(user, pageable);
    }

    @Override
    public User getUserById(long userId) {
        return userService.getById(userId);
    }

    @Override
    public Post getPostById(long postId) {
        return postService.getById(postId);
    }

    @Override
    public Page<Comment> getCommentForPost(Post post, Pageable pageable) {
        return commentService.getCommentForPost(post, pageable);
    }

    @Override
    public void deleteCommentById(long commentId) {
        commentService.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deletePostWithComments(long postId) {
        Post post = postService.getById(postId);
        commentService.deleteByPost(post);
        postService.deletePost(post);
    }

    @Override
    public void deleteUserWithPosts(long userId) {
        User user = userService.getById(userId);
        postService.getByUser(user).forEach(post -> deletePostWithComments(post.getId()));
        userService.deleteUser(user);
    }

    @Override
    public void changeAdminPassword(String newPassword) {
        User user = userService.getByEmail(ADMIN_EMAIL);
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveOrUpdateUser(user);
    }

    @Override
    public void resetAdminPassword() {
        changeAdminPassword(ADMIN_PASSWORD);
    }
}
