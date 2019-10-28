package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.domain.User;

/**
 * Created by uc on 10/19/2019
 */
public interface VerificationTokenService {

    VerificationToken getByToken(String token);

    void createTokenForUser(User user, String token);
}
