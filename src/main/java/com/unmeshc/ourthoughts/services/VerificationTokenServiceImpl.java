package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.repositories.VerificationTokenRepository;
import org.springframework.stereotype.Service;

/**
 * Created by uc on 10/19/2019
 */
@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken getByToken(String token) {
        return verificationTokenRepository.findByToken(token).orElse(null);
    }

    @Override
    public void createTokenForUser(User user, String token) {
        VerificationToken verificationToken =
                VerificationToken.builder().token(token).user(user).build();
        verificationTokenRepository.save(verificationToken);
    }
}