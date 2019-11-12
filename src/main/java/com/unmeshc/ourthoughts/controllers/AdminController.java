package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.services.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    static final String REDIRECT_ADMIN_ALL_USER_DELETE = "redirect:/admin/all/user?delete=true";
    static final String USER_POSTS = "admin/userPosts";
    static final String POST_COMMENTS = "admin/postComments";

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/reset/password")
    public String resetPassword() {
        adminService.resetAdminPassword();
        return REDIRECT_LOGIN;
    }

    @PostMapping("/change/password")
    public String changePassword(@RequestParam(value = "newPassword") String newPassword,
                                 HttpServletRequest request) {
        adminService.changeAdminPasswordAndLogout(newPassword, request);
        return REDIRECT_LOGIN;
    }

    @GetMapping("/console.html")
    public String adminConsole() {
        return REDIRECT_ADMIN_ALL_USER;
    }

    @GetMapping("/all/user")
    public String allUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "0") int size,
                           @RequestParam(value = "delete", defaultValue = "false") boolean delete,
                           Model model) {
        model.addAttribute("userAdminListDto", adminService.getAllUsers(page, size, delete));
        return CONSOLE;
    }

    @GetMapping("/all/user/{userId}/delete")
    public String deleteUser(@PathVariable long userId) {
        adminService.deleteUserWithPostsById(userId);
        return REDIRECT_ADMIN_ALL_USER_DELETE;
    }

    @GetMapping("/user/{userId}/post")
    public String showUserPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "0") int size,
                                @RequestParam(value = "delete", defaultValue = "false") boolean delete,
                                @PathVariable long userId,
                                Model model) {
        model.addAttribute("userPostAdminDto",
                adminService.getPostsForUser(userId, page, size, delete));
        return USER_POSTS;
    }

    @GetMapping("/user/{userId}/post/{postId}/delete")
    public String deletePost(@PathVariable long userId,
                             @PathVariable long postId) {
        adminService.deletePostWithCommentsById(postId);
        return "redirect:/admin/user/" + userId + "/post?delete=true";
    }

    @GetMapping("/post/{postId}/comment")
    public String showPostComments(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "0") int size,
                                   @RequestParam(value = "delete", defaultValue = "false") boolean delete,
                                   @PathVariable long postId,
                                   Model model) {
        model.addAttribute("postCommentAdminDto",
                adminService.getCommentsForPost(postId, page, size, delete));
        return POST_COMMENTS;
    }

    @GetMapping("/post/{postId}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable long postId,
                                @PathVariable long commentId) {
        adminService.deleteCommentById(commentId);
        return "redirect:/admin/post/" + postId + "/comment?delete=true";
    }
}
