package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.configurations.SecurityUtils;
import com.unmeshc.ourthoughts.controllers.pagination.SearchPostPageTracker;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.PostSearchDto;
import com.unmeshc.ourthoughts.services.CommentService;
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

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final ControllerUtils controllerUtils;
    private final SecurityUtils securityUtils;
    private final SearchPostPageTracker searchPostPageTracker;

    public PostController(PostService postService,
                          UserService userService,
                          CommentService commentService,
                          ControllerUtils controllerUtils,
                          SecurityUtils securityUtils,
                          SearchPostPageTracker searchPostPageTracker) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.controllerUtils = controllerUtils;
        this.securityUtils = securityUtils;
        this.searchPostPageTracker = searchPostPageTracker;
    }

    @GetMapping("/visitor/post/search")
    public String search(@RequestParam("page") Optional<Integer> page,
                         @RequestParam("size") Optional<Integer> size,
                         @RequestParam("search") Optional<String> search,
                         Model model) {

        String searchValue = search.orElse(searchPostPageTracker.getSearchValue());
        if (!searchValue.equalsIgnoreCase(searchPostPageTracker.getSearchValue())) {
            searchPostPageTracker.reset();
        }

        int currentPage = page.orElse(searchPostPageTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("creationDateTime").descending()); // zero based page

        Page<Post> postPage = postService.getPostsLikeTitle(searchValue, pageable);
        searchPostPageTracker.setCurrentPage(postPage.getNumber() + 1);
        searchPostPageTracker.setSearchValue(searchValue);

        List<PostSearchDto> postSearchDtos =
                controllerUtils.convertToPostSearchDtoList(postPage.getContent());

        model.addAttribute("postSearchDtos", controllerUtils.adjustTitleAndBody(postSearchDtos));
        model.addAttribute("currentPage", searchPostPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", searchPostPageTracker.getPageNumbersForPagination(postPage));

        return "index";
    }

    @GetMapping("/visitor/post/{postId}/details")
    public String viewPostDetails(@PathVariable long postId,
                                  Model model) {
        model.addAttribute("postDetails", postService.getPostDetailsById(postId));
        return "post/postDetails";
    }

    @GetMapping("/visitor/post/{postId}/photo")
    public void obtainPostPhoto(@PathVariable long postId, HttpServletResponse response) {
        Post post = postService.getById(postId);
        if (post == null) {
            log.error("Post not found with id - " + postId + " during obtain post photo");
            return;
        }

        byte[] bytes = controllerUtils.convertIntoByteArray(post.getPhoto());
        controllerUtils.copyBytesToResponse(response, bytes);
    }

    @GetMapping("/visitor/user/{userId}/image")
    public void obtainUserImage(@PathVariable long userId, HttpServletResponse response) {
        User user = userService.getById(userId);
        if (user == null) {
            log.error("User not found with id - " + userId + " during obtain user photo");
            return;
        }

        byte[] bytes = controllerUtils.convertIntoByteArray(user.getImage());
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
