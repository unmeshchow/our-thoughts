package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.configurations.SecurityUtils;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.CommentService;
import com.unmeshc.ourthoughts.services.ImageService;
import com.unmeshc.ourthoughts.services.PostService;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * Created by uc on 10/24/2019
 */
@Slf4j
@Controller
public class PostController {

    private final SecurityUtils securityUtils;
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final PostPageTracker postPageTracker;
    private final ControllerUtils controllerUtils;
    private final ImageService imageService;

    public PostController(SecurityUtils securityUtils,
                          UserService userService,
                          PostService postService,
                          CommentService commentService,
                          PostPageTracker postPageTracker,
                          ControllerUtils controllerUtils,
                          ImageService imageService) {
        this.securityUtils = securityUtils;
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.postPageTracker = postPageTracker;
        this.controllerUtils = controllerUtils;
        this.imageService = imageService;
    }

    @GetMapping("/visitor/post/search")
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

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("creationDateTime").descending()); // zero based page

        Page<Post> postPage = postService.getPostsLikeTitle(searchValue, pageable);
        postPageTracker.setCurrentPage(postPage.getNumber() + 1);
        postPageTracker.setSearchValue(searchValue);

        List<PostCommand> postCommands =
                controllerUtils.convertToPostCommandList(postPage.getContent());

        model.addAttribute("posts", controllerUtils.adjustTitleAndBody(postCommands));
        model.addAttribute("currentPage", postPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", postPageTracker.getPageNumbersForPagination(postPage));

        return "index";
    }

    @GetMapping("/visitor/post/{postId}/details")
    public String viewPostDetails(@PathVariable long postId,
                                  Model model) {
        model.addAttribute("post", postService.getPostDetailsById(postId));
        return "post/postDetails";
    }

    @GetMapping("/visitor/post/{postId}/photo")
    public void obtainPostPhoto(@PathVariable long postId, HttpServletResponse response) {
        Post post = postService.getById(postId);
        if (post == null) {
            log.error("Post not found with id - " + postId + " during obtain post photo");
            return;
        }

        byte[] bytes = imageService.convertIntoByteArray(post.getPhoto());
        controllerUtils.copyBytesToResponse(response, bytes);
    }

    @GetMapping("/visitor/user/{userId}/image")
    public void obtainUserImage(@PathVariable long userId, HttpServletResponse response) {
        User user = userService.getById(userId);
        if (user == null) {
            log.error("User not found with id - " + userId + " during obtain user photo");
            return;
        }

        byte[] bytes = imageService.convertIntoByteArray(user.getImage());
        controllerUtils.copyBytesToResponse(response, bytes);
    }

    @PostMapping("/post/{postId}/comment/add")
    public String addComment(@PathVariable long postId,
                             @RequestParam("comment") Optional<String> comment) {

        String userComment = comment.orElse("");
        User user = userService.getByEmail(securityUtils.getEmailFromSecurityContext());
        Post post = postService.getById(postId);

        commentService.saveCommentOfUserForPost(userComment, user, post);

        return "redirect:/visitor/post/" + postId + "/details";
    }
}
