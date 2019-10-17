package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.TokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by uc on 10/16/2019
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(UserService userService,
                                   EmailService emailService,
                                   TokenRepository tokenRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(UserCommand userCommand, HttpServletRequest request) {
        User user = userService.saveUser(userCommand);
        emailService.sendAccountActivateLink(user, request);

        return user;
    }

    @Override
    public Token getToken(String token) {
        return tokenRepository.findByToken(token).orElse(null);
    }

    @Override
    public void activateUser(User user) {
        user.setActive(true);
        userService.updateUser(user);
    }

    @Override
    public User getUser(String email) {
        return userService.getByEmail(email);
    }

    @Override
    public void resetPassword(User user, HttpServletRequest request) {
        emailService.sendPasswordResetLink(user, request);
    }

    @Override
    public void updatePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userService.updateUser(user);
    }
}
