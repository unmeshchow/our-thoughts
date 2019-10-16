package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.services.RegistrationService;
import com.unmeshc.ourthoughts.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Created by uc on 10/14/2019
 */
@Controller
public class RegistrationController {

    private final UserService userService;
    private final RegistrationService registrationService;

    public RegistrationController(UserService userService,
                                  RegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
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
                                       BindingResult result,
                                       HttpServletRequest request) {
        if (result.hasErrors()) {
            return "register/registrationForm";
        }

        if (userService.isEmailExists(userCommand.getEmail())) {
            result.rejectValue("email","EmailExists");
            return "register/registrationForm";
        }

        registrationService.saveUser(userCommand, request);

        return "redirect:/registration/success";
    }

    @GetMapping("/registration/success")
    public String successRegistration() {
        return "register/registrationSuccess";
    }

    @GetMapping("/registration/confirm")
    public String activeRegistration(@RequestParam("token") String token) {
        Token foundToken = registrationService.getToken(token);
        if (foundToken == null || foundToken.isExpired()) {
            return "redirect:/registration/confirm/bad";
        }

        registrationService.activateUser(foundToken.getUser());

        return "redirect:/login";
    }

    @GetMapping("/registration/confirm/bad")
    public String badToken() {
        return "register/badToken";
    }

    @GetMapping("/password/reset/form")
    public String showPasswordResetForm() {
        return "register/passwordResetForm";
    }
}
