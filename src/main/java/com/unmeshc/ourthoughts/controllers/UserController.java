package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.configurations.security.SecurityUtils;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by uc on 10/19/2019
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    static final String REDIRECT_INDEX = "redirect:/index.html";
    static final String MY_PROFILE = "user/myProfile";
    static final String UPLOAD_IMAGE = "user/uploadImage";
    static final String REDIRECT_USER_PROFILE = "redirect:/user/profile";
    static final String CREATE_POST_FORM = "user/createPostForm";

    private final UserService userService;
    private final SecurityUtils securityUtils;
    private final ControllerUtils controllerUtils;

    public UserController(UserService userService,
                          SecurityUtils securityUtils,
                          ControllerUtils controllerUtils) {
        this.securityUtils = securityUtils;
        this.userService = userService;
        this.controllerUtils = controllerUtils;
    }

    @ModelAttribute("user")
    public User loggedInUser() {
        return userService.getUserByEmail(securityUtils.getEmailFromSecurityContext());
    }

    @GetMapping("/create/post/form")
    public String showCreatePostForm(Model model) {
        model.addAttribute("postCommand", PostCommand.builder().build());
        return CREATE_POST_FORM;
    }

    @PostMapping("/create/post")
    public String processCreatePost(@Valid PostCommand postCommand,
                                    BindingResult result,
                                    @ModelAttribute("user") User user) {

        if (result.hasErrors()) {
            return CREATE_POST_FORM;
        }

        if (controllerUtils.isNotCorrectPostPhoto(postCommand.getMultipartFile())) {
            result.rejectValue("multipartFile", "NotCorrect");
            return CREATE_POST_FORM;
        }

        userService.savePostForUser(user, postCommand);

        return REDIRECT_INDEX;
    }

    @GetMapping("/profile")
    public String showProfile(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("userProfileDto", userService.getUserProfile(user));
        return MY_PROFILE;
    }

    @GetMapping("/change/image/form")
    public String showChangeImageForm() {
        return UPLOAD_IMAGE;
    }

    @PostMapping("/change/image")
    public String changeImage(@ModelAttribute("user") User user,
                              @RequestParam("myImage") MultipartFile imageFile,
                              Model model) {

        if (controllerUtils.isNotCorrectUserImage(imageFile)) {
            model.addAttribute("error", true);
            return UPLOAD_IMAGE;
        } else {
            model.addAttribute("error", false);
        }

        userService.changeImageForUser(user, imageFile);

        return REDIRECT_USER_PROFILE;
    }

    @GetMapping("/get/image")
    public void obtainImage(@ModelAttribute("user") User user, HttpServletResponse response) {
        controllerUtils.copyBytesToResponse(response, userService.getImageForUser(user));
    }

    @PostMapping("/comment/post/{postId}/add")
    public String addComment(@PathVariable long postId,
                             @RequestParam(value = "comment", defaultValue = "") String comment,
                             @ModelAttribute("user") User user) {

        userService.saveCommentOfUserForPost(comment, user, postId);
        return "redirect:/visitor/post/" + postId + "/details";
    }
}