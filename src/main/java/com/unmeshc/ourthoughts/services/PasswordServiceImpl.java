package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by uc on 10/19/2019
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    private final EmailService emailService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public PasswordServiceImpl(EmailService emailService,
                               UserService userService,
                               PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void verifyResetPassword(User user, HttpServletRequest request) {
        emailService.sendPasswordResetLink(user, request);
    }

    @Override
    public void updatePassword(User user, PasswordCommand passwordCommand) {
        user.setPassword(passwordEncoder.encode(passwordCommand.getPassword()));
        userService.saveOrUpdateUser(user);
    }
}