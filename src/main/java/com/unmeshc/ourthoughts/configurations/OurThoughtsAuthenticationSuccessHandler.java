package com.unmeshc.ourthoughts.configurations;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by uc on 10/17/2019
 */
@Slf4j
@Component
public class OurThoughtsAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public OurThoughtsAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication)
                                        throws IOException {

        User user = userService.getByEmail(authentication.getName());
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute("fullName", user.getFirstName() + " "
                + user.getLastName());

        log.debug("Name: " + httpSession.getAttribute("fullName"));

        boolean admin = false;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                admin = true;
            }
        }

        // set response status code to SC_OK
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        if (admin) {
            httpServletResponse.sendRedirect("/console.html");
        } else {
            httpServletResponse.sendRedirect("/index.html");
        }
    }
}
