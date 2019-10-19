package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by uc on 10/16/2019
 */
public interface EmailService {

    void sendAccountActivationLink(User user, HttpServletRequest request);

    void sendPasswordResetLink(User user, HttpServletRequest request);
}
