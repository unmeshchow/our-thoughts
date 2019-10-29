package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.domain.VerificationToken;
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
    private final VerificationTokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public PasswordServiceImpl(EmailService emailService,
                               UserService userService,
                               VerificationTokenService tokenService,
                               PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void verifyResetPasswordForUser(User user, HttpServletRequest request) {
        emailService.sendPasswordResetLinkForUser(user, request);
    }

    @Override
    public void updatePasswordForUser(User user, PasswordCommand passwordCommand) {
        user.setPassword(passwordEncoder.encode(passwordCommand.getPassword()));
        userService.saveOrUpdate(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userService.getByEmail(email);
    }

    @Override
    public VerificationToken getVerificationTokenByToken(String token) {
        return tokenService.getByToken(token);
    }
}