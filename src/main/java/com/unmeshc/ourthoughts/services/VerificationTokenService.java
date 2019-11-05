package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;

/**
 * Created by uc on 10/19/2019
 */
public interface VerificationTokenService {

    VerificationToken getByToken(String token);

    void createTokenForUser(User user, String token);
}
