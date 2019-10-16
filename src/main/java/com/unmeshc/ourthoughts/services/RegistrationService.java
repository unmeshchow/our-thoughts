package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by uc on 10/16/2019
 */
public interface RegistrationService {

    User saveUser(UserCommand userCommand, HttpServletRequest request);

    Token getToken(String token);

    void activateUser(User user);

    User getUser(String email);

    void resetPassword(User user, HttpServletRequest request);
}
