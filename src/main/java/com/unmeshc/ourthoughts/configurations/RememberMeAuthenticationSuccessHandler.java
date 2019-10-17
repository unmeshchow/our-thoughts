package com.unmeshc.ourthoughts.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by uc on 10/17/2019
 */
@Slf4j
@Component
public class RememberMeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationSuccessHandlerUtils authenticationSuccessHandlerUtils;

    public RememberMeAuthenticationSuccessHandler(AuthenticationSuccessHandlerUtils authenticationSuccessHandlerUtils) {
        this.authenticationSuccessHandlerUtils = authenticationSuccessHandlerUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) {

        authenticationSuccessHandlerUtils.saveFullNameInHttpSession(httpServletRequest, authentication);
    }
}
