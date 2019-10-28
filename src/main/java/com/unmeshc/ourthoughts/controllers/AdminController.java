package com.unmeshc.ourthoughts.controllers;

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

    private final UserPageAdminTracker userPageTracker;
    private final AdminService adminService;
    private final ControllerUtils controllerUtils;
    private final PostPageAdminTracker postPageAdminTracker;
    private final CommentPageAdminTracker commentPageAdminTracker;

    public AdminController(UserPageAdminTracker userPageTracker,
                           AdminService adminService,
                           ControllerUtils controllerUtils,
                           PostPageAdminTracker postPageAdminTracker,
                           CommentPageAdminTracker commentPageAdminTracker) {
        this.userPageTracker = userPageTracker;
        this.adminService = adminService;
        this.controllerUtils = controllerUtils;
        this.postPageAdminTracker = postPageAdminTracker;
        this.commentPageAdminTracker = commentPageAdminTracker;
    }

    @PostMapping("/change/password")
    public String changePassword(@RequestParam(value = "newPassword") String newPassword,
                                 HttpServletRequest request) throws ServletException {
        adminService.changeAdminPassword(newPassword);
        request.logout();
        return "redirect:/login";
    }

    @GetMapping("/console.html")
    public String adminConsole() {
        return "redirect:/admin/all/user";
    }

    @GetMapping("/all/user")
    public String allUsers(@RequestParam("page") Optional<Integer> page,
                           @RequestParam("size") Optional<Integer> size,
                           @RequestParam(value = "delete", defaultValue = "no") String delete,
                           Model model) {

        int currentPage = page.orElse(userPageTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("lastName").ascending().and(Sort.by("firstName").descending())); // zero based page

        Page<User> userPage = adminService.getAllUsers(pageable);

        // Fix the last page problem - this method is called after deletion
        if (delete.equalsIgnoreCase("yes") &&
                userPage.getContent().isEmpty() &&
                currentPage > 1) {

            currentPage -= 1;
            pageable = PageRequest.of((currentPage - 1), pageSize,
                    Sort.by("lastName").ascending().and(Sort.by("firstName").descending())); // zero based page
            userPage = adminService.getAllUsers(pageable);
        }

        userPageTracker.setCurrentPage(userPage.getNumber() + 1);

        List<UserAdminDto> userAdminDtos =
                controllerUtils.convertToAdminUserDtoList(userPage.getContent());

        model.addAttribute("userAdminDtos", userAdminDtos);
        model.addAttribute("currentPage", userPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", userPageTracker.getPageNumbersForPagination(userPage));

        return "admin/console";
    }

    @GetMapping("/all/user/{userId}/delete")
    public String deleteUser(@PathVariable long userId) {
        adminService.deleteUserWithPosts(userId);
        return "redirect:/admin/all/user?delete=yes";
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

        if (postPageAdminTracker.getUserId() != userId) {
            postPageAdminTracker.reset();
        }

        int currentPage = page.orElse(postPageAdminTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("creationDateTime").descending()); // zero based page

        Page<Post> postPage = adminService.getPostForUser(user, pageable);

        // Fix the last page problem - this method is called after deletion
        if (delete.equalsIgnoreCase("yes") &&
                postPage.getContent().isEmpty() &&
                currentPage > 1) {

            currentPage -= 1;
            pageable = PageRequest.of((currentPage - 1), pageSize,
                    Sort.by("creationDateTime").descending()); // zero based page
            postPage = adminService.getPostForUser(user, pageable);
        }

        postPageAdminTracker.setCurrentPage(postPage.getNumber() + 1);
        postPageAdminTracker.setUserId(userId);

        List<PostAdminDto> postAdminDtos =
                controllerUtils.convertToPostAdminDtoList(postPage.getContent());
        UserPostAdminDto userPostAdminDto = UserPostAdminDto.builder().id(userId).
                firstName(user.getFirstName()).postAdminDtos(postAdminDtos).build();

        model.addAttribute("userPostAdminDto", userPostAdminDto);
        model.addAttribute("currentPage", postPageAdminTracker.getCurrentPage());
        model.addAttribute("pageNumbers", postPageAdminTracker.getPageNumbersForPagination(postPage));

        return "admin/userPosts";
    }

    @GetMapping("/user/{userId}/post/{postId}/delete")
    public String deletePost(@PathVariable long userId,
                             @PathVariable long postId) {
        adminService.deletePostWithComments(postId);
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

        if (commentPageAdminTracker.getPostId() != postId) {
            commentPageAdminTracker.reset();
        }

        int currentPage = page.orElse(commentPageAdminTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("addingDateTime").descending()); // zero based page

        Page<Comment> commentPage = adminService.getCommentForPost(post, pageable);

        // Fix the last page problem - this method is called after deletion
        if (delete.equalsIgnoreCase("yes") &&
                   commentPage.getContent().isEmpty() &&
                   currentPage > 1) {

            currentPage -= 1;
            pageable = PageRequest.of((currentPage - 1), pageSize,
                    Sort.by("addingDateTime").descending());
            commentPage = adminService.getCommentForPost(post, pageable);
        }

        commentPageAdminTracker.setCurrentPage(commentPage.getNumber() + 1);
        commentPageAdminTracker.setPostId(postId);

        List<CommentAdminDto> commentAdminDtos =
                controllerUtils.convertToCommentAdminDtoList(commentPage.getContent());
        PostCommentAdminDto postCommentAdminDto = PostCommentAdminDto.builder().id(postId)
                .title(post.getTitle()).commentAdminDtos(commentAdminDtos).build();

        model.addAttribute("postCommentAdminDto", postCommentAdminDto);
        model.addAttribute("currentPage", commentPageAdminTracker.getCurrentPage());
        model.addAttribute("pageNumbers", commentPageAdminTracker.getPageNumbersForPagination(commentPage));

        return "admin/postComments";
    }

    @GetMapping("/post/{postId}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable long postId,
                                @PathVariable long commentId) {
        adminService.deleteCommentById(commentId);
        return "redirect:/admin/post/" + postId + "/comment?delete=yes";
    }

}
