package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;

/**
 * Created by uc on 10/15/2019
 */
public interface UserService {

    boolean isEmailExists(String email);

    void createToken(User user, String token);

    User saveOrUpdateUser(User user);

    User getByEmail(String email);
}
