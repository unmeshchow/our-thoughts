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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.unmeshc.ourthoughts.TestLiterals.ID;
import static com.unmeshc.ourthoughts.TestLiterals.TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VerificationTokenServiceImplTest {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private VerificationTokenServiceImpl verificationTokenService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getVerificationTokenByTokenNull() {
        when(verificationTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        assertThat(verificationTokenService.getVerificationTokenByToken(TOKEN)).isNull();
    }

    @Test
    public void getVerificationTokenByToken() {
        VerificationToken verificationToken = VerificationToken.builder().id(ID).build();
        when(verificationTokenRepository.findByToken(TOKEN))
                .thenReturn(Optional.of(verificationToken));

        VerificationToken foundVerificationToken =
                verificationTokenService.getVerificationTokenByToken(TOKEN);

        assertThat(foundVerificationToken).isEqualTo(verificationToken);
    }

    @Test
    public void createVerificationTokenForUser() {
        User user = User.builder().id(ID).build();

        verificationTokenService.createVerificationTokenForUser(user, TOKEN);

        ArgumentCaptor<VerificationToken> verificationTokenArgumentCaptor =
                ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository).save(verificationTokenArgumentCaptor.capture());

        VerificationToken verificationToken = verificationTokenArgumentCaptor.getValue();
        assertThat(verificationToken.getUser()).isEqualTo(user);
        assertThat(verificationToken.getToken()).isEqualTo(TOKEN);
    }

    @Test
    public void getAllVerificationTokens() {
        List<VerificationToken> verificationTokens =
                Arrays.asList(VerificationToken.builder().id(ID).build());
        when(verificationTokenRepository.findAll()).thenReturn(verificationTokens);

        List<VerificationToken> foundVerificationTokens =
                verificationTokenService.getAllVerificationTokens();

        assertThat(foundVerificationTokens).isEqualTo(verificationTokens);
    }

    @Test
    public void deleteExpiredVerificationTokes() {
        List<VerificationToken> expiredVerificationTokens =
                Arrays.asList(VerificationToken.builder().id(ID).build());

        verificationTokenService.deleteExpiredVerificationTokens(expiredVerificationTokens);

        verify(verificationTokenRepository).deleteAll(expiredVerificationTokens);
    }
}