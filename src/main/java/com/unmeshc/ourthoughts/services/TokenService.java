package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.domain.User;

/**
 * Created by uc on 10/19/2019
 */
public interface TokenService {

    Token getByToken(String token);

    void createTokenForUser(User user, String token);
}
