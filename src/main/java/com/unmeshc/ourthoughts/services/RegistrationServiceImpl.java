package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by uc on 10/16/2019
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final EmailService emailService;

    public RegistrationServiceImpl(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public User saveUser(UserCommand userCommand, HttpServletRequest request) {
        User user = userService.saveUser(userCommand);
        emailService.sendAccountActivateLink(user, request);

        return user;
    }
}
