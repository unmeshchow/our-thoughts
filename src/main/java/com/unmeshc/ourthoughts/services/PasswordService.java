package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by uc on 10/19/2019
 */
public interface PasswordService {

    void verifyResetPasswordForUser(User user, HttpServletRequest request);

    void updatePasswordForUser(User user, PasswordCommand passwordCommand);

    User getUserByEmail(String email);

    VerificationToken getVerificationTokenByToken(String token);
}
