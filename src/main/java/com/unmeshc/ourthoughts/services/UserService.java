package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by uc on 10/15/2019
 */
public interface UserService {

    boolean isEmailExists(String email);

    User saveOrUpdate(User user);

    User getByEmail(String email);

    User getById(long userId);

    void delete(User user);

    Page<User> getAllExceptAdmin(String adminEmail, Pageable pageable);
}
