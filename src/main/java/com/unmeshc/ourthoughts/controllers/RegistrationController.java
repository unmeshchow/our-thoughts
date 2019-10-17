package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.exceptions.NotFoundException;
import com.unmeshc.ourthoughts.services.RegistrationService;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Arrays;

/**
 * Created by uc on 10/14/2019
 */
@Slf4j
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

    @GetMapping({"/registration/confirm/bad", "/password/reset/confirm/bad"})
    public String badToken() {
        return "register/badToken";
    }

    @GetMapping("/password/reset/form")
    public String showPasswordResetForm() {
        return "register/passwordResetForm";
    }

    @GetMapping("/password/reset/send")
    public String processPasswordReset(@RequestParam("email") String email,
                                       HttpServletRequest request) {

        User user = registrationService.getUser(email);
        if (user == null || !user.getActive()) {
            throw new NotFoundException("User not found with email: " + email);
        }

        registrationService.resetPassword(user, request);

        return "redirect:/password/reset/success";
    }

    @GetMapping("/password/reset/success")
    public String passwordResetSuccess() {
        return "register/passwordResetSuccess";
    }

    @GetMapping("/password/reset/confirm")
    public String acceptPasswordReset(@RequestParam("token") String token) {
        Token foundToken = registrationService.getToken(token);
        if (foundToken == null || foundToken.isExpired()) {
            return "redirect:/password/reset/confirm/bad";
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(foundToken.getUser(), null,
                Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/password/reset/update/form";
    }

    @GetMapping("/password/reset/update/form")
    public String showPasswordUpdateForm(Model model) {
        model.addAttribute("passwordCommand", PasswordCommand.builder().build());
        return "register/passwordUpdateForm";
    }
}
