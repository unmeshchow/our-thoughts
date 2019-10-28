package com.unmeshc.ourthoughts.controllers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by uc on 10/23/2019
 */
@Slf4j
@Getter
@Setter
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CommentPageAdminTracker implements PageTracker {

    private long postId = 0;
    private int currentPage = 1; // one based page
    private int startPage = 1;
    private int endPage = 4; // maximum number of pagination links at a time

    private final ControllerUtils controllerUtils;

    public CommentPageAdminTracker(ControllerUtils controllerUtils) {
        this.controllerUtils = controllerUtils;
    }

    Set<Integer> getPageNumbersForPagination(Page<?> postPage) {
        controllerUtils.adjustPagination(postPage, this);
        return IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toSet());
    }

    void reset() {
        currentPage = 1;
        startPage = 1;
        endPage = 4;
    }
}
