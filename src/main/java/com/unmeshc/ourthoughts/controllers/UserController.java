package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.configurations.SecurityUtils;
import com.unmeshc.ourthoughts.converters.UserToUserCommand;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.ImageService;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by uc on 10/19/2019
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserToUserCommand userToUserCommand;
    private final ImageService imageService;
    private final SecurityUtils securityUtils;

    public UserController(UserService userService,
                          UserToUserCommand userToUserCommand,
                          ImageService imageService,
                          SecurityUtils securityUtils) {
        this.imageService = imageService;
        this.securityUtils = securityUtils;
        this.userService = userService;
        this.userToUserCommand = userToUserCommand;
    }

    @ModelAttribute("user")
    public User loggedInUser() {
        String email = securityUtils.getEmailFromSecurityContext();

        return userService.getByEmail(email);
    }

    @GetMapping("/profile")
    public String showProfile(@ModelAttribute("user") User user, Model model) {
        UserCommand userCommand = userToUserCommand.convert(user);
        model.addAttribute("userCommand", userCommand);

        return "user/myProfile";
    }

    @GetMapping("/change/image/form")
    public String showChangeImageForm() {
        return "user/uploadImage";
    }

    @PostMapping("/change/image")
    public String changeImage(@ModelAttribute("user") User user,
                              @RequestParam("myImage") MultipartFile imageFile) {

        Byte[] bytes = imageService.convertIntoByteArray(imageFile);
        user.setImage(bytes);
        userService.saveOrUpdateUser(user);

        return "redirect:/user/profile";
    }

    @GetMapping("/get/image")
    public void obtainImage(@ModelAttribute("user") User user, HttpServletResponse response) {
        byte[] bytes = imageService.convertIntoByteArray(user.getImage());

        response.setContentType("image/jpeg");
        InputStream inputStream = new ByteArrayInputStream((bytes));

        try {
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception exc) {
            log.error("Error occurred during copying input stream into output stream");
            throw new RuntimeException("Error occurred in retrieving image, try again.");
        }
    }
}