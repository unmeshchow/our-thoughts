package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.services.exceptions.BadVerificationTokenException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by uc on 10/19/2019
 */
public interface PasswordService {

    void verifyResetPasswordForUserByEmailing(String email, HttpServletRequest request);

    void changePasswordForPrivilegedUser(PasswordCommand passwordCommand);

    void checkAndSetChangePasswordPrivilege(String token) throws BadVerificationTokenException;
}
