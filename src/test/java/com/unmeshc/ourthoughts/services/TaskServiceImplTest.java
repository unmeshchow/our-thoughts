package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskServiceImplTest {

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void deleteExpiredTokensAndInactiveUsers() {
        User activeUser = User.builder().id(1L).active(true).build();
        User inactiveUser = User.builder().id(2L).active(false).build();
        User inactiveUser2 = User.builder().id(3L).active(false).build();
        User activeUser2 = User.builder().id(4L).active(true).build();

        VerificationToken verificationTokenExpired = VerificationToken.builder().id(1L)
                .expiryDate(LocalDateTime.now().minusDays(2L)).user(activeUser).build();
        VerificationToken verificationTokenExpired2 = VerificationToken.builder().id(2L)
                .expiryDate(LocalDateTime.now().minusDays(2L)).user(inactiveUser).build();
        VerificationToken verificationTokenNotExpired = VerificationToken.builder().id(3L)
                .expiryDate(LocalDateTime.now().plusDays(1L)).user(inactiveUser2).build();
        VerificationToken verificationTokenNotExpired2 = VerificationToken.builder().id(3L)
                .expiryDate(LocalDateTime.now().plusDays(1L)).user(activeUser2).build();

        List<User> inactiveUsers = Arrays.asList(inactiveUser);
        List<VerificationToken> verificationTokens = Arrays.asList(verificationTokenExpired,
            verificationTokenExpired2, verificationTokenNotExpired, verificationTokenNotExpired2);
        List<VerificationToken> expiredVerificationTokens = Arrays.asList(verificationTokenExpired,
                verificationTokenExpired2);

        when(verificationTokenService.getAllVerificationTokens()).thenReturn(verificationTokens);

        taskService.deleteExpiredVerificationTokensAndInactiveUsers();

        verify(verificationTokenService).deleteExpiredVerificationTokens(expiredVerificationTokens);
        verify(userService).deleteInactiveUsers(inactiveUsers);
    }
}