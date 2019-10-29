package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.controllers.pagination.AdminCommentPageTracker;
import com.unmeshc.ourthoughts.controllers.pagination.AdminPostPageTracker;
import com.unmeshc.ourthoughts.controllers.pagination.AdminUserPageTracker;
import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.*;
import com.unmeshc.ourthoughts.exceptions.NotFoundException;
import com.unmeshc.ourthoughts.services.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Created by uc on 10/26/2019
 */
@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    static final String REDIRECT_LOGIN = "redirect:/login";
    static final String REDIRECT_ADMIN_ALL_USER = "redirect:/admin/all/user";
    static final String CONSOLE = "admin/console";
    static final String REDIRECT_ADMIN_ALL_USER_DELETE = "redirect:/admin/all/user?delete=yes";
    static final String USER_POSTS = "admin/userPosts";
    static final String POST_COMMENTS = "admin/postComments";

    private final AdminService adminService;
    private final ControllerUtils controllerUtils;
    private final AdminPostPageTracker adminPostPageTracker;
    private final AdminCommentPageTracker adminCommentPageTracker;
    private final AdminUserPageTracker adminUserPageTracker;

    public AdminController(AdminUserPageTracker adminUserPageTracker,
                           AdminService adminService,
                           ControllerUtils controllerUtils,
                           AdminPostPageTracker adminPostPageTracker,
                           AdminCommentPageTracker adminCommentPageTracker) {
        this.adminUserPageTracker = adminUserPageTracker;
        this.adminService = adminService;
        this.controllerUtils = controllerUtils;
        this.adminPostPageTracker = adminPostPageTracker;
        this.adminCommentPageTracker = adminCommentPageTracker;
    }

    @GetMapping("/reset/password")
    public String resetPassword() {
        adminService.resetAdminPassword();
        return REDIRECT_LOGIN;
    }


    @PostMapping("/change/password")
    public String changePassword(@RequestParam(value = "newPassword") String newPassword,
                                 HttpServletRequest request) throws ServletException {
        adminService.changeAdminPassword(newPassword);
        request.logout();
        return REDIRECT_LOGIN;
    }

    @GetMapping("/console.html")
    public String adminConsole() {
        return REDIRECT_ADMIN_ALL_USER;
    }

    @GetMapping("/all/user")
    public String allUsers(@RequestParam("page") Optional<Integer> page,
                           @RequestParam("size") Optional<Integer> size,
                           @RequestParam(value = "delete", defaultValue = "no") String delete,
                           Model model) {

        int currentPage = page.orElse(adminUserPageTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("lastName").ascending().and(Sort.by("firstName").descending())); // zero based page

        Page<User> userPage = adminService.getAllUsers(pageable);

        // Fix the last page problem since the last page can be deleted
        if (delete.equalsIgnoreCase("yes") &&
                userPage.getContent().isEmpty() &&
                currentPage > 1) {

            currentPage -= 1;
            pageable = PageRequest.of((currentPage - 1), pageSize,
                    Sort.by("lastName").ascending().and(Sort.by("firstName").descending())); // zero based page
            userPage = adminService.getAllUsers(pageable);
        }

        adminUserPageTracker.setCurrentPage(userPage.getNumber() + 1);

        List<UserAdminDto> userAdminDtos =
                controllerUtils.convertToAdminUserDtoList(userPage.getContent());

        model.addAttribute("userAdminDtos", userAdminDtos);
        model.addAttribute("currentPage", adminUserPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", adminUserPageTracker.getPageNumbersForPagination(userPage));

        return CONSOLE;
    }

    @GetMapping("/all/user/{userId}/delete")
    public String deleteUser(@PathVariable long userId) {
        adminService.deleteUserWithPostsById(userId);
        return REDIRECT_ADMIN_ALL_USER_DELETE;
    }

    @GetMapping("/user/{userId}/post")
    public String showUserPosts(@RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size,
                                @RequestParam(value = "delete", defaultValue = "no") String delete,
                                @PathVariable long userId,
                                Model model) {

        User user = adminService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id - " + userId);
        }

        if (adminPostPageTracker.getUserId() != userId) {
            adminPostPageTracker.reset();
        }

        int currentPage = page.orElse(adminPostPageTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("creationDateTime").descending()); // zero based page

        Page<Post> postPage = adminService.getPostsByUser(user, pageable);

        // Fix the last page problem since the last page can be deleted
        if (delete.equalsIgnoreCase("yes") &&
                postPage.getContent().isEmpty() &&
                currentPage > 1) {

            currentPage -= 1;
            pageable = PageRequest.of((currentPage - 1), pageSize,
                    Sort.by("creationDateTime").descending()); // zero based page
            postPage = adminService.getPostsByUser(user, pageable);
        }

        adminPostPageTracker.setCurrentPage(postPage.getNumber() + 1);
        adminPostPageTracker.setUserId(userId);

        List<PostAdminDto> postAdminDtos =
                controllerUtils.convertToPostAdminDtoList(postPage.getContent());
        UserPostAdminDto userPostAdminDto = UserPostAdminDto.builder().id(userId).
                firstName(user.getFirstName()).postAdminDtos(postAdminDtos).build();

        model.addAttribute("userPostAdminDto", userPostAdminDto);
        model.addAttribute("currentPage", adminPostPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", adminPostPageTracker.getPageNumbersForPagination(postPage));

        return USER_POSTS;
    }

    @GetMapping("/user/{userId}/post/{postId}/delete")
    public String deletePost(@PathVariable long userId,
                             @PathVariable long postId) {
        adminService.deletePostWithCommentsById(postId);
        return "redirect:/admin/user/" + userId + "/post?delete=yes";
    }

    @GetMapping("/post/{postId}/comment")
    public String showPostComments(@RequestParam("page") Optional<Integer> page,
                                   @RequestParam("size") Optional<Integer> size,
                                   @RequestParam(value = "delete", defaultValue = "no") String delete,
                                   @PathVariable long postId,
                                   Model model) {

        Post post = adminService.getPostById(postId);
        if (post == null) {
            throw new NotFoundException("Post not found with id - " + postId);
        }

        if (adminCommentPageTracker.getPostId() != postId) {
            adminCommentPageTracker.reset();
        }

        int currentPage = page.orElse(adminCommentPageTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("addingDateTime").descending()); // zero based page

        Page<Comment> commentPage = adminService.getCommentsByPost(post, pageable);

        // Fix the last page problem since the last page can be deleted
        if (delete.equalsIgnoreCase("yes") &&
                   commentPage.getContent().isEmpty() &&
                   currentPage > 1) {

            currentPage -= 1;
            pageable = PageRequest.of((currentPage - 1), pageSize,
                    Sort.by("addingDateTime").descending());
            commentPage = adminService.getCommentsByPost(post, pageable);
        }

        adminCommentPageTracker.setCurrentPage(commentPage.getNumber() + 1);
        adminCommentPageTracker.setPostId(postId);

        List<CommentAdminDto> commentAdminDtos =
                controllerUtils.convertToCommentAdminDtoList(commentPage.getContent());
        PostCommentAdminDto postCommentAdminDto = PostCommentAdminDto.builder().id(postId)
                .title(post.getTitle()).commentAdminDtos(commentAdminDtos).build();

        model.addAttribute("postCommentAdminDto", postCommentAdminDto);
        model.addAttribute("currentPage", adminCommentPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", adminCommentPageTracker.getPageNumbersForPagination(commentPage));

        return POST_COMMENTS;
    }

    @GetMapping("/post/{postId}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable long postId,
                                @PathVariable long commentId) {
        adminService.deleteCommentById(commentId);
        return "redirect:/admin/post/" + postId + "/comment?delete=yes";
    }

}
