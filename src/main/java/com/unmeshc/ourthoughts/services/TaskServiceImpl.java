package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by uc on 11/5/2019
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final VerificationTokenService verificationTokenService;
    private final UserService userService;

    public TaskServiceImpl(VerificationTokenService verificationTokenService,
                           UserService userService) {
        this.verificationTokenService = verificationTokenService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void deleteExpiredVerificationTokensAndInactiveUsers() {
        List<VerificationToken> expiredTokens =
                verificationTokenService.getAllVerificationTokens()
                .stream()
                .filter(verificationToken -> verificationToken.isExpired())
                .collect(Collectors.toList());

        List<User> inactiveUsers = expiredTokens
                .stream()
                .map(verificationToken -> verificationToken.getUser())
                .filter(user -> user.getActive() == false)
                .collect(Collectors.toList());

        verificationTokenService.deleteExpiredVerificationTokens(expiredTokens);
        userService.deleteInactiveUsers(inactiveUsers);
    }
}
