package com.unmeshc.ourthoughts.services.exceptions;

/**
 * Created by uc on 10/16/2019
 */
public class EmailNotSentException extends RuntimeException {

    public EmailNotSentException(String message) {
        super(message);
    }
}
