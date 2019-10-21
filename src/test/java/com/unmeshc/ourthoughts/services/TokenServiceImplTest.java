package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.TokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TokenServiceImplTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenServiceImpl service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getByToken() {
        when(tokenRepository.findByToken(anyString())).thenReturn(
                Optional.of(Token.builder().id(1L).build()));

        Token token = service.getByToken("erd4567h#dg8");

        assertThat(token).isNotNull();
        verify(tokenRepository).findByToken("erd4567h#dg8");
    }

    @Test
    public void getByTokenNull() {
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        Token token = service.getByToken("erd4567h#dg8");

        assertThat(token).isNull();
        verify(tokenRepository).findByToken("erd4567h#dg8");
    }

    @Test
    public void createTokenForUser() {
        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        String token = "33dg4ffg";
        Token verificationToken = Token.builder().id(1L).user(user).token(token).build();

        service.createTokenForUser(user, token);

        verify(tokenRepository).save(verificationToken);
    }
}