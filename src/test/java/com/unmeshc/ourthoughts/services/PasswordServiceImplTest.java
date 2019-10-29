package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;

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

    @InjectMocks
    private PasswordServiceImpl service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void verifyResetPassword() {
        User user = User.builder().build();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        service.verifyResetPasswordForUser(user, request);

        verify(emailService).sendPasswordResetLinkForUser(user, request);
    }

    @Test
    public void updatePassword() {
        User user = User.builder().id(1L).build();
        PasswordCommand passwordCommand = PasswordCommand.builder().password("unmesh").build();
        when(passwordEncoder.encode(anyString())).thenReturn("unmesh");

        service.updatePasswordForUser(user, passwordCommand);

        user.setPassword("unmesh");

        verify(userService).saveOrUpdate(user);
    }
}