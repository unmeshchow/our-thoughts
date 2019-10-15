package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * Created by uc on 10/14/2019
 */
@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void dataBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("id");
    }

    @GetMapping("/registration/form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userCommand", UserCommand.builder().build());
        return "register/registrationForm";
    }

    @PostMapping("/registration/save")
    public String saveRegistrationData(@Valid UserCommand userCommand,
                                       BindingResult result) {
        if (result.hasErrors()) {
            return "register/registrationForm";
        }

        if (userService.isEmailExists(userCommand.getEmail())) {
            result.rejectValue("email","EmailExists");
            return "register/registrationForm";
        }

        userService.saveUser(userCommand);

        return "success";
    }
}
