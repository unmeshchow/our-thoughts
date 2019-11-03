package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.repositories.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void deleteExpiredTokens() {
        List<VerificationToken> verificationTokens = new ArrayList<>();

        verificationTokenRepository.findAll().forEach(verificationToken -> {
            if (verificationToken.isExpired()) {
                verificationTokens.add(verificationToken);
            }
        });

        if (!verificationTokens.isEmpty()) {
            verificationTokenRepository.deleteAll(verificationTokens);
        }
    }
}