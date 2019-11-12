package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.services.exceptions.BadVerificationTokenException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by uc on 10/16/2019
 */
public interface RegistrationService {

    void saveUserAndVerifyByEmailing(UserCommand userCommand, HttpServletRequest request);

    boolean isUserEmailExists(String email);

    void activateUserByVerificationToken(String token) throws BadVerificationTokenException ;
}