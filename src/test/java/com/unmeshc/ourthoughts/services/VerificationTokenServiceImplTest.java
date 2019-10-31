package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.repositories.VerificationTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VerificationTokenServiceImplTest {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private VerificationTokenServiceImpl service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getByTokenNull() {
        when(verificationTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        assertThat(service.getByToken("token")).isNull();
    }

    @Test
    public void getByToken() {
        String token = "token";
        VerificationToken verificationToken = VerificationToken.builder().id(1L).build();
        when(verificationTokenRepository.findByToken(token))
                .thenReturn(Optional.of(verificationToken));

        VerificationToken foundVerificationToken = service.getByToken(token);

        assertThat(foundVerificationToken).isEqualTo(verificationToken);
        verify(verificationTokenRepository).findByToken(token);
    }

    @Test
    public void createTokenForUser() {
        User user = User.builder().id(1L).build();
        String token = "token";

        service.createTokenForUser(user, token);

        ArgumentCaptor<VerificationToken> verificationTokenArgumentCaptor =
                ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository).save(verificationTokenArgumentCaptor.capture());
        VerificationToken verificationToken = verificationTokenArgumentCaptor.getValue();
        assertThat(verificationToken.getUser()).isEqualTo(user);
        assertThat(verificationToken.getToken()).isEqualTo(token);
    }
}