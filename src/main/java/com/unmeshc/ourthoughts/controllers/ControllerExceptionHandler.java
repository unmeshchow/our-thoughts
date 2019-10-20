package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.exceptions.EmailNotSentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by uc on 10/20/2019
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(EmailNotSentException.class)
    public ModelAndView handleEmailException(Exception exc) {
        log.error("Handling email not sent exception");
        log.error(exc.getMessage());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/emailException");
        modelAndView.addObject("exception", exc);

        return modelAndView;
    }
}
