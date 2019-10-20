package com.unmeshc.ourthoughts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by uc on 10/16/2019
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class internalServcerError extends RuntimeException {

    public internalServcerError(String message) {
        super(message);
    }
}
