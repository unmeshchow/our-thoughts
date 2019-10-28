package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.services.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Created by uc on 10/14/2019
 */
@Slf4j
@Controller
@RequestMapping("/registration")
public class RegistrationController {

    static final String REGISTRATION_FORM = "register/registrationForm";
    static final String REDIRECT_REGISTRATION_SUCCESS = "redirect:/registration/success";
    static final String REGISTRATION_SUCCESS = "register/registrationSuccess";
    static final String REDIRECT_LOGIN = "redirect:/login";
    static final String REDIRECT_REGISTRATION_CONFIRM_BAD = "redirect:/registration/confirm/bad";
    static final String CONFIRM_BAD = "register/badToken";

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @InitBinder
    public void dataBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("id");
    }

    @GetMapping("/form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userCommand", UserCommand.builder().build());

        return REGISTRATION_FORM;
    }

    @PostMapping("/save")
    public String saveRegistrationData(@Valid UserCommand userCommand,
                                       BindingResult result,
                                       HttpServletRequest request) {
        if (result.hasErrors()) {
            return REGISTRATION_FORM;
        }

        if (registrationService.isUserEmailExists(userCommand.getEmail())) {
            result.rejectValue("email","EmailExists");
            return REGISTRATION_FORM;
        }

        registrationService.saveAndVerifyUser(userCommand, request);

        return REDIRECT_REGISTRATION_SUCCESS;
    }

    @GetMapping("/success")
    public String successRegistration() {
        return REGISTRATION_SUCCESS;
    }

    @GetMapping("/confirm")
    public String activeRegistration(@RequestParam("token") String token) {
        VerificationToken foundToken = registrationService.getVerificationTokenByToken(token);

        if (foundToken == null || foundToken.isExpired()) {
            return REDIRECT_REGISTRATION_CONFIRM_BAD;
        }

        registrationService.activateUser(foundToken.getUser());

        return REDIRECT_LOGIN;
    }

    @GetMapping("/confirm/bad")
    public String badToken() {
        return CONFIRM_BAD;
    }
}
