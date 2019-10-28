package com.unmeshc.ourthoughts.controllers.pagination;

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
}
