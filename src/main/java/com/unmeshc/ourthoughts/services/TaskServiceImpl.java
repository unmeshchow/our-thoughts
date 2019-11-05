package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.repositories.UserRepository;
import com.unmeshc.ourthoughts.repositories.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uc on 11/5/2019
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(VerificationTokenRepository verificationTokenRepository,
                           UserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void deleteExpiredTokensAndInactiveUsers() {
        List<VerificationToken> expiredTokens = new ArrayList<>();
        List<User> inactiveUsers = new ArrayList<>();

        verificationTokenRepository.findAll().forEach(verificationToken -> {
            if (verificationToken.isExpired()) {
                expiredTokens.add(verificationToken);

                if (!verificationToken.getUser().getActive()) {
                    inactiveUsers.add(verificationToken.getUser());
                }
            }
        });

        if (!expiredTokens.isEmpty()) {
            verificationTokenRepository.deleteAll(expiredTokens);
        }

        if (!inactiveUsers.isEmpty()) {
            userRepository.deleteAll(inactiveUsers);
        }
    }
}
