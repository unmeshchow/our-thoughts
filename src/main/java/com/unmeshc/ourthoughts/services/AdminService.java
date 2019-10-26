package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by uc on 10/26/2019
 */
public interface AdminService {

    void createAdminUser();

    boolean isAdminExists();

    Page<User> getAllUsers(Pageable pageable);
}
