package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.configurations.SecurityUtils;
import com.unmeshc.ourthoughts.controllers.pagination.SearchPostPageTracker;
import com.unmeshc.ourthoughts.converters.UserToUserProfileDto;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.PostService;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
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

    static final String MY_PROFILE = "user/myProfile";
    static final String UPLOAD_IMAGE = "user/uploadImage";
    static final String REDIRECT_USER_PROFILE = "redirect:/user/profile";
    static final String CREATE_POST_FORM = "user/createPostForm";

    private final UserService userService;
    private final SecurityUtils securityUtils;
    private final PostService postService;
    private final ControllerUtils controllerUtils;
    private final SearchPostPageTracker postPageTracker;
    private final UserToUserProfileDto userToUserProfileDto;

    public UserController(UserService userService,
                          SecurityUtils securityUtils,
                          PostService postService,
                          ControllerUtils controllerUtils,
                          SearchPostPageTracker postPageTracker,
                          UserToUserProfileDto userToUserProfileDto) {
        this.securityUtils = securityUtils;
        this.userService = userService;
        this.postService = postService;
        this.controllerUtils = controllerUtils;
        this.postPageTracker = postPageTracker;
        this.userToUserProfileDto = userToUserProfileDto;
    }

    @InitBinder
    public void dataBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("id");
    }

    @ModelAttribute("user")
    public User loggedInUser() {
        String email = securityUtils.getEmailFromSecurityContext();
        return userService.getByEmail(email);
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

        if (controllerUtils.isNotCorrectPostPhoto(postCommand.getPhoto())) {
            result.rejectValue("photo", "NotCorrect");
            return CREATE_POST_FORM;
        }

        postCommand.setPostPhoto(controllerUtils.convertIntoByteArray(postCommand.getPhoto()));
        postService.savePostForUser(user, postCommand);
        postPageTracker.newPost();

        return "redirect:/index.html";
    }

    @GetMapping("/profile")
    public String showProfile(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("userProfileDto", userToUserProfileDto.convert(user));
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

        Byte[] bytes = controllerUtils.convertIntoByteArray(imageFile);
        user.setImage(bytes);
        userService.saveOrUpdate(user);

        return REDIRECT_USER_PROFILE;
    }

    @GetMapping("/get/image")
    public void obtainImage(@ModelAttribute("user") User user, HttpServletResponse response) {
        byte[] bytes = controllerUtils.convertIntoByteArray(user.getImage());
        controllerUtils.copyBytesToResponse(response, bytes);
    }
}