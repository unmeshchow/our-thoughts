package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.services.ImageService;
import com.unmeshc.ourthoughts.services.PostService;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by uc on 10/13/2019
 */
@Slf4j
@Controller
public class IndexController {

    private final PostService postService;
    private final ImageService imageService;
    private final ControllerUtils controllerUtils;
    private final PostPageTracker postPageTracker;
    private final UserService userService;

    public IndexController(PostService postService,
                           ImageService imageService,
                           ControllerUtils controllerUtils,
                           PostPageTracker postPageTracker,
                           UserService userService) {
        this.postService = postService;
        this.imageService = imageService;
        this.controllerUtils = controllerUtils;
        this.postPageTracker = postPageTracker;
        this.userService = userService;
    }

    @GetMapping("/index.html")
    public String index() {
        return "redirect:/visitor/post/search";
    }
}
