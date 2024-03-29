package com.unmeshc.ourthoughts.configurations.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by uc on 10/17/2019
 */
@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SecurityUtils authenticationSuccessHandlerUtils;

    public LoginSuccessHandler(SecurityUtils authenticationSuccessHandlerUtils) {
        this.authenticationSuccessHandlerUtils = authenticationSuccessHandlerUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication)
                                        throws IOException {

        authenticationSuccessHandlerUtils.saveFullNameIntoHttpSession(httpServletRequest, authentication);

        boolean admin = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ADMIN".equals(authority.getAuthority())) {
                admin = true;
            }
        }

        // set response status code to SC_OK
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        if (admin) {
            httpServletResponse.sendRedirect("/admin/console.html");
        }
        else {
            httpServletResponse.sendRedirect("/index.html");
        }
    }
}
