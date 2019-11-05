package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.exceptions.EmailNotSentException;
import com.unmeshc.ourthoughts.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by uc on 10/20/2019
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(EmailNotSentException.class)
    public ModelAndView handleEmailException(Exception exc) {
        log.error("Handling email not sent exception");
        log.error(exc.getMessage());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/emailException");
        modelAndView.addObject("exception", exc);

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException(Exception exc) {
        log.error("Handling not found sent exception");
        log.error(exc.getMessage());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/notFoundException");
        modelAndView.addObject("exception", exc);

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleInternalServerError(Exception exc) {
        log.error("Error occurred");
        log.error(exc.getMessage());
        log.error(exc.toString());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/internalServerError");
        modelAndView.addObject("exception", exc);

        return modelAndView;
    }
}
