package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.TokenRepository;
import org.springframework.stereotype.Service;

/**
 * Created by uc on 10/19/2019
 */
@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Token getByToken(String token) {
        return tokenRepository.findByToken(token).orElse(null);
    }

    @Override
    public void createTokenForUser(User user, String token) {
        Token verificationToken = Token.builder().token(token).user(user).build();
        tokenRepository.save(verificationToken);
    }
}