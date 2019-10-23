package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.domain.Post;
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
public class PostPageTracker {

    private String searchValue = "";

    private int currentPage = 1;
    private int startPage = 1;
    private int endPage = 4; // maximum number of pagination links at a time

    Set<Integer> getPageNumbersForPagination(Page<Post> postPage) {

        // Calculate start and end page for the dynamic pagination
        if (postPage.getTotalPages() < endPage) {
            endPage = postPage.getTotalPages();
        } else if (startPage != 1 &&
                  (currentPage == startPage || currentPage == (startPage + 1))) {
            startPage -= 1;
            endPage -= 1;
        } else if (endPage != postPage.getTotalPages() &&
                  (currentPage == endPage || currentPage == (endPage - 1))) {
            startPage += 1;
            endPage += 1;
        }

        log.debug("Start: " + startPage);
        log.debug("End: " + endPage);
        log.debug("Total: " + postPage.getTotalPages());

        return IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toSet());
    }
}
