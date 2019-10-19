package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.TokenRepository;
import com.unmeshc.ourthoughts.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by uc on 10/15/2019
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public UserServiceImpl(UserRepository userRepository,
                           TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void createToken(User user, String token) {
        Token activationToken = Token.builder().user(user).token(token).build();
        tokenRepository.save(activationToken);
    }

    @Override
    public User saveOrUpdateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
