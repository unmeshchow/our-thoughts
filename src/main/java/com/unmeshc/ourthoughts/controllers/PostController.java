package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by uc on 10/24/2019
 */
@Slf4j
@Controller
@RequestMapping("/visitor")
public class PostController {

    static final String INDEX = "index";
    static final String POST_DETAILS = "post/postDetails";

    private final PostService postService;
    private final ControllerUtils controllerUtils;

    public PostController(PostService postService,
                          ControllerUtils controllerUtils) {
        this.postService = postService;
        this.controllerUtils = controllerUtils;
    }

    @GetMapping("/post/search")
    public String search(@RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "0") int size,
                         @RequestParam(value = "title", defaultValue = "") String title,
                         Model model) {

        model.addAttribute("postSearchListDto",
                postService.getPostsByTitleLike(page, size, title));
        return INDEX;
    }

    @GetMapping("/post/{postId}/details")
    public String viewPostDetails(@PathVariable long postId,
                                  Model model) {

        model.addAttribute("postDetails", postService.getPostDetailsById(postId));
        return POST_DETAILS;
    }

    @GetMapping("/post/{postId}/photo")
    public void obtainPostPhoto(@PathVariable long postId, HttpServletResponse response) {
        controllerUtils.copyBytesToResponse(response, postService.getPostPhotoById(postId));
    }

    @GetMapping("/user/{userId}/image")
    public void obtainUserImage(@PathVariable long userId, HttpServletResponse response) {
        controllerUtils.copyBytesToResponse(response, postService.getUserImageById(userId));
    }
}