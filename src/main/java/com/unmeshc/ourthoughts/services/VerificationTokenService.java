package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;

import java.util.List;

/**
 * Created by uc on 10/19/2019
 */
public interface VerificationTokenService {

    VerificationToken getVerificationTokenByToken(String token);

    void createVerificationTokenForUser(User user, String token);

    List<VerificationToken> getAllVerificationTokens();

    void deleteExpiredVerificationTokens(List<VerificationToken> expiredVerificationTokens);
}
