package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.services.PasswordService;
import com.unmeshc.ourthoughts.services.exceptions.BadVerificationTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Created by uc on 10/19/2019
 */
@Slf4j
@Controller
@RequestMapping("/password")
public class PasswordController {

    static final String PASSWORD_RESET_FORM = "register/passwordResetForm";
    static final String REDIRECT_PASSWORD_RESET_SUCCESS = "redirect:/password/reset/success";
    static final String PASSWORD_RESET_SUCCESS = "register/passwordResetSuccess";
    static final String REDIRECT_PASSWORD_RESET_CONFIRM_BAD = "redirect:/password/reset/confirm/bad";
    static final String REDIRECT_PASSWORD_RESET_UPDATE_FORM = "redirect:/password/reset/update/form";
    static final String BAD_TOKEN = "register/badToken";
    static final String PASSWORD_UPDATE_FORM = "register/passwordUpdateForm";
    static final String REDIRECT_LOGIN = "redirect:/login";

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping("/reset/form")
    public String showPasswordResetForm() {
        return PASSWORD_RESET_FORM;
    }

    @GetMapping("/reset/send")
    public String processPasswordReset(@RequestParam("email") String email,
                                       HttpServletRequest request) {
        passwordService.verifyResetPasswordForUserByEmailing(email, request);
        return REDIRECT_PASSWORD_RESET_SUCCESS;
    }

    @GetMapping("/reset/success")
    public String passwordResetSuccess() {
        return PASSWORD_RESET_SUCCESS;
    }

    @GetMapping("/reset/confirm")
    public String acceptPasswordReset(@RequestParam("token") String token) {
        try {
            passwordService.checkAndSetChangePasswordPrivilege(token);
        } catch (BadVerificationTokenException exception) {
            return REDIRECT_PASSWORD_RESET_CONFIRM_BAD;
        }
        return REDIRECT_PASSWORD_RESET_UPDATE_FORM;
    }

    @GetMapping("/reset/confirm/bad")
    public String badToken() {
        return BAD_TOKEN;
    }

    @GetMapping("/reset/update/form")
    public String showPasswordUpdateForm(Model model) {
        model.addAttribute("passwordCommand", PasswordCommand.builder().build());
        return PASSWORD_UPDATE_FORM;
    }

    @PostMapping("/reset/update")
    public String resetPassword(@Valid PasswordCommand passwordCommand,
                                BindingResult result) {

        if (result.hasErrors()) {
            return PASSWORD_UPDATE_FORM;
        }
        passwordService.changePasswordForPrivilegedUser(passwordCommand);
        return REDIRECT_LOGIN;
    }
}