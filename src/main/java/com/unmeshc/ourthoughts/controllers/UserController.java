package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.converters.UserToUserCommand;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by uc on 10/19/2019
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserToUserCommand userToUserCommand;

    public UserController(UserService userService,
                          UserToUserCommand userToUserCommand) {
        log.debug("UserController");
        this.userService = userService;
        this.userToUserCommand = userToUserCommand;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        log.debug("showProfile");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.getByEmail(email);
        UserCommand userCommand = userToUserCommand.convert(user);

        model.addAttribute("userCommand", userCommand);

        return "user/myProfile";
    }


}
