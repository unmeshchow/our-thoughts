package com.unmeshc.ourthoughts.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by uc on 10/15/2019
 */
@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "register/login";
    }
}
