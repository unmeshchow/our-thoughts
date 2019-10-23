package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.exceptions.NotFoundException;
import com.unmeshc.ourthoughts.services.ImageService;
import com.unmeshc.ourthoughts.services.PostService;
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

    public IndexController(PostService postService,
                           ImageService imageService,
                           ControllerUtils controllerUtils,
                           PostPageTracker postPageTracker) {
        this.postService = postService;
        this.imageService = imageService;
        this.controllerUtils = controllerUtils;
        this.postPageTracker = postPageTracker;
    }

    @GetMapping("/index.html")
    public String index() {
        return "redirect:/post/search";
    }

    @GetMapping("/post/search")
    public String search(@RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size,
                         @RequestParam("search") Optional<String> search,
                         Model model) {

        int currentPage = page.orElse(postPageTracker.getCurrentPage());
        int pageSize = size.orElse(1);
        String searchValue = search.orElse(postPageTracker.getSearchValue());
        Pageable pageable = PageRequest.of((currentPage - 1), pageSize); // zero based

        Page<Post> postPage = postService.getPostsLikeTitle(searchValue, pageable);
        postPageTracker.setCurrentPage(postPage.getNumber() + 1); // one based
        postPageTracker.setSearchValue(searchValue);

        model.addAttribute("posts", controllerUtils.adjustTitleAndBody(postPage.getContent()));
        model.addAttribute("currentPage", postPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", postPageTracker.getPageNumbersForPagination(postPage));

        return "index";
    }

    //"/post/{postId}/photo"
    //"/post/{postId}/view"
    //"/post/search"

    @GetMapping("/post/{postId}/photo")
    public void obtainImage(@PathVariable long postId, HttpServletResponse response) {
        Post post = postService.getPostById(postId);
        if (post == null) {
            throw new NotFoundException("Post not found with id - " + postId);
        }

        byte[] bytes = imageService.convertIntoByteArray(post.getPhoto());
        controllerUtils.copyBytesToResponse(response, bytes);
    }
}
