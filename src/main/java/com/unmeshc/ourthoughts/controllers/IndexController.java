package com.unmeshc.ourthoughts.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by uc on 10/13/2019
 */
@Slf4j
@Controller
public class IndexController {

    @GetMapping("/index.html")
    public String index() {
        return "redirect:/visitor/post/search";
    }
}
