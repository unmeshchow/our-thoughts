package com.unmeshc.ourthoughts.configurations.security;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by uc on 10/17/2019
 */
@Slf4j
@Component
public class SecurityUtils {

    private final UserService userService;

    public SecurityUtils(UserService userService) {
        this.userService = userService;
    }

    public void saveFullNameIntoHttpSession(HttpServletRequest httpServletRequest,
                                            Authentication authentication) {

        User user = userService.getUserByEmail(authentication.getName());
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute("fullName", user.getFirstName() + " "
                + user.getLastName());
    }

    public String getEmailFromSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
