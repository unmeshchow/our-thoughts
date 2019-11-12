package com.unmeshc.ourthoughts.services.pagination;

import org.springframework.data.domain.Page;

/**
 * Created by uc on 10/26/2019
 */
public interface PageTracker {

    void setCurrentPage(int currentPage);

    int getCurrentPage();

    void setStartPage(int startPage);

    int getStartPage();

    void setEndPage(int endPage);

    int getEndPage();

    default void adjustPagination(Page<?> currentPage, PageTracker pageTracker) {

        // Calculate start and end page for the dynamic pagination
        if (currentPage.getTotalPages() < pageTracker.getEndPage()) {
            pageTracker.setEndPage(currentPage.getTotalPages());

        } else if (pageTracker.getStartPage() != 1 &&
                (pageTracker.getCurrentPage() == pageTracker.getStartPage() ||
                        pageTracker.getCurrentPage() == (pageTracker.getStartPage() + 1))) {

            pageTracker.setStartPage(pageTracker.getStartPage() - 1);
            pageTracker.setEndPage(pageTracker.getEndPage() - 1);

        } else if (pageTracker.getEndPage() != currentPage.getTotalPages() &&
                (pageTracker.getCurrentPage() == pageTracker.getEndPage() ||
                        (pageTracker.getCurrentPage() == (pageTracker.getEndPage() -1)))) {

            pageTracker.setStartPage(pageTracker.getStartPage() + 1);
            pageTracker.setEndPage(pageTracker.getEndPage() + 1);
        }
    }
}
