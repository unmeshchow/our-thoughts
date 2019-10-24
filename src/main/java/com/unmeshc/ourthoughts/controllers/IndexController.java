package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.ImageService;
import com.unmeshc.ourthoughts.services.PostService;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

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
        return "redirect:/everyone/post/search";
    }

    @GetMapping("/everyone/post/search")
    public String search(@RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size,
                         @RequestParam("search") Optional<String> search,
                         Model model) {

        String searchValue = search.orElse(postPageTracker.getSearchValue());
        if (!searchValue.equalsIgnoreCase(postPageTracker.getSearchValue())) {
            postPageTracker.reset();
        }

        int currentPage = page.orElse(postPageTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize); // zero based

        Page<Post> postPage = postService.getPostsLikeTitle(searchValue, pageable);
        postPageTracker.setCurrentPage(postPage.getNumber() + 1); // one based
        postPageTracker.setSearchValue(searchValue);

        model.addAttribute("posts", controllerUtils.adjustTitleAndBody(postPage.getContent()));
        model.addAttribute("currentPage", postPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", postPageTracker.getPageNumbersForPagination(postPage));

        return "index";
    }

    @GetMapping("/everyone/post/{postId}/details")
    public String viewPostDetails(@PathVariable long postId,
                                  Model model) {
        model.addAttribute("post", postService.getPostDetailsById(postId));
        return "post/postDetails";
    }

    @GetMapping("/everyone/post/{postId}/photo")
    public void obtainPostPhoto(@PathVariable long postId, HttpServletResponse response) {
        Post post = postService.getById(postId);
        if (post == null) {
            log.error("Post not found with id - " + postId + " during obtain post photo");
            return;
        }

        byte[] bytes = imageService.convertIntoByteArray(post.getPhoto());
        controllerUtils.copyBytesToResponse(response, bytes);
    }

    @GetMapping("/everyone/user/{userId}/image")
    public void obtainUserImage(@PathVariable long userId, HttpServletResponse response) {
        User user = userService.getById(userId);
        if (user == null) {
            log.error("User not found with id - " + userId + " during obtain user photo");
            return;
        }

        byte[] bytes = imageService.convertIntoByteArray(user.getImage());
        controllerUtils.copyBytesToResponse(response, bytes);
    }
}
