package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PasswordServiceImplTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationTokenService verificationTokenService;

    @InjectMocks
    private PasswordServiceImpl service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void verifyResetPasswordForUser() {
        User user = User.builder().build();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        service.verifyResetPasswordForUser(user, request);

        verify(emailService).sendPasswordResetLinkForUser(user, request);
    }

    @Test
    public void updatePasswordForUser() {
        User user = User.builder().id(1L).build();
        PasswordCommand passwordCommand = PasswordCommand.builder().password("unmesh").build();
        when(passwordEncoder.encode(anyString())).thenReturn("unmesh");

        service.updatePasswordForUser(user, passwordCommand);

        user.setPassword("unmesh");

        verify(userService).saveOrUpdate(user);
    }

    @Test
    public void getUserByEmail() {
        User user = User.builder().id(1L).build();
        when(userService.getByEmail(anyString())).thenReturn(user);

        User foundUser = service.getUserByEmail("unmesh@gmail.com");

        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    public void getVerificationTokenByToken() {
        VerificationToken verificationToken = VerificationToken.builder().id(1L).build();
        when(verificationTokenService.getByToken(anyString())).thenReturn(verificationToken);

        VerificationToken foundVerificationToken = service.getVerificationTokenByToken("token");

        assertThat(foundVerificationToken).isEqualTo(verificationToken);
        verify(verificationTokenService).getByToken("token");
    }
}