package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.exceptions.NotFoundException;
import com.unmeshc.ourthoughts.services.PasswordService;
import com.unmeshc.ourthoughts.services.TokenService;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;

/**
 * Created by uc on 10/19/2019
 */
@Slf4j
@Controller
@RequestMapping("/password")
public class PasswordController {

    public static final String PASSWORD_RESET_FORM = "register/passwordResetForm";
    public static final String REDIRECT_PASSWORD_RESET_SUCCESS = "redirect:/password/reset/success";
    public static final String PASSWORD_RESET_SUCCESS = "register/passwordResetSuccess";
    public static final String REDIRECT_PASSWORD_RESET_CONFIRM_BAD = "redirect:/password/reset/confirm/bad";
    public static final String REDIRECT_PASSWORD_RESET_UPDATE_FORM = "redirect:/password/reset/update/form";
    public static final String REGISTER_BAD_TOKEN = "register/badToken";
    public static final String PASSWORD_UPDATE_FORM = "register/passwordUpdateForm";
    public static final String REDIRECT_LOGIN = "redirect:/login";

    private final PasswordService passwordService;
    private final UserService userService;
    private final TokenService tokenService;

    public PasswordController(PasswordService passwordService,
                              UserService userService,
                              TokenService tokenService) {
        this.passwordService = passwordService;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("/reset/form")
    public String showPasswordResetForm() {
        return PASSWORD_RESET_FORM;
    }

    @GetMapping("/reset/send")
    public String processPasswordReset(@RequestParam("email") String email,
                                       HttpServletRequest request) {

        User user = userService.getByEmail(email);
        if (user == null || !user.getActive()) {
            throw new NotFoundException("User not found with email: " + email);
        }

        passwordService.verifyResetPassword(user, request);

        return REDIRECT_PASSWORD_RESET_SUCCESS;
    }

    @GetMapping("/reset/success")
    public String passwordResetSuccess() {
        return PASSWORD_RESET_SUCCESS;
    }

    @GetMapping("/reset/confirm")
    public String acceptPasswordReset(@RequestParam("token") String token) {
        Token foundToken = tokenService.getByToken(token);
        if (foundToken == null || foundToken.isExpired()) {
            return REDIRECT_PASSWORD_RESET_CONFIRM_BAD;
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(foundToken.getUser(), null,
                Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        return REDIRECT_PASSWORD_RESET_UPDATE_FORM;
    }

    @GetMapping("/reset/confirm/bad")
    public String badToken() {
        return REGISTER_BAD_TOKEN;
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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        passwordService.updatePassword(user, passwordCommand);

        return REDIRECT_LOGIN;
    }
}
