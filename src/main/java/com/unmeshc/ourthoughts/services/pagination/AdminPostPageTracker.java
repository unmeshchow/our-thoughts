package com.unmeshc.ourthoughts.services.pagination;

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
public class AdminPostPageTracker implements PageTracker {

    private long userId = 0;
    private int currentPage = 1; // one based page
    private int startPage = 1;
    private int endPage = 5; // maximum number of pagination links at a time

    public Set<Integer> getPageNumbersForPagination(Page<?> postPage) {
        adjustPagination(postPage, this);
        return IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toSet());
    }

    public void reset() {
        currentPage = 1;
        startPage = 1;
        endPage = 4;
    }
}
