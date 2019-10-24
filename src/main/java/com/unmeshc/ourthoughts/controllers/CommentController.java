package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.configurations.SecurityUtils;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.CommentService;
import com.unmeshc.ourthoughts.services.PostService;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Created by uc on 10/24/2019
 */
@Slf4j
@Controller
@RequestMapping("/post")
public class CommentController {

    private final SecurityUtils securityUtils;
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    public CommentController(SecurityUtils securityUtils,
                             UserService userService,
                             PostService postService,
                             CommentService commentService) {
        this.securityUtils = securityUtils;
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping("/{postId}/comment/add")
    public String addComment(@PathVariable long postId,
                             @RequestParam("comment") Optional<String> comment) {

        String userComment = comment.orElse("");
        User user = userService.getByEmail(securityUtils.getEmailFromSecurityContext());
        Post post = postService.getById(postId);

        commentService.saveCommentOfUserForPost(userComment, user, post);

        return "redirect:/everyone/post/" + postId + "/details";
    }
}
