package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.services.exceptions.BadVerificationTokenException;
import com.unmeshc.ourthoughts.services.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.unmeshc.ourthoughts.TestLiterals.*;
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
    private PasswordServiceImpl passwordService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NotFoundException.class)
    public void verifyResetPasswordForUserNotFound() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(userService.getUserByEmail(anyString())).thenThrow(NotFoundException.class);
        passwordService.verifyResetPasswordForUserByEmailing(EMAIL, request);
    }

    @Test(expected = NotFoundException.class)
    public void verifyResetPasswordForUserNotFoundAdminUser() {
        Role role = Role.builder().name(ADMIN).build();
        Set<Role> roles = new HashSet<>(Arrays.asList(role));
        User user = User.builder().id(ID).roles(roles).build();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        passwordService.verifyResetPasswordForUserByEmailing(EMAIL, request);
    }

    @Test(expected = NotFoundException.class)
    public void verifyResetPasswordForUserNotFoundInactiveUser() {
        Role role = Role.builder().name(USER).build();
        Set<Role> roles = new HashSet<>(Arrays.asList(role));
        User user = User.builder().id(ID).roles(roles).active(false).build();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        passwordService.verifyResetPasswordForUserByEmailing(EMAIL, request);
    }

    @Test
    public void verifyResetPasswordForUser() {
        Role role = Role.builder().name(USER).build();
        Set<Role> roles = new HashSet<>(Arrays.asList(role));
        User user = User.builder().id(ID).active(true).roles(roles).build();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        passwordService.verifyResetPasswordForUserByEmailing(EMAIL, request);

        verify(emailService).sendPasswordResetLinkForUser(user, request);
    }

    @Test(expected = BadVerificationTokenException.class)
    public void verifyAndSetChangePasswordPrivilegeNotFoundToken()
            throws BadVerificationTokenException {

        when(verificationTokenService.getVerificationTokenByToken(anyString()))
                .thenReturn(null);

        passwordService.checkAndSetChangePasswordPrivilege(TOKEN);
    }

    @Test(expected = BadVerificationTokenException.class)
    public void verifyAndSetChangePasswordPrivilegeExpiredToken()
            throws BadVerificationTokenException {

        VerificationToken verificationToken = VerificationToken.builder()
                .expiryDate(LocalDateTime.now().minusDays(2L)).build();
        when(verificationTokenService.getVerificationTokenByToken(anyString()))
                .thenReturn(verificationToken);

        passwordService.checkAndSetChangePasswordPrivilege(TOKEN);
    }

    @Test
    public void verifyAndSetChangePasswordPrivilege() throws BadVerificationTokenException{
        VerificationToken verificationToken = VerificationToken.builder().expiryDate(
                LocalDateTime.now().plusDays(1L)).build();
        when(verificationTokenService.getVerificationTokenByToken(anyString()))
                .thenReturn(verificationToken);

        passwordService.checkAndSetChangePasswordPrivilege(TOKEN);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(
                o -> "CHANGE_PASSWORD_PRIVILEGE".equalsIgnoreCase(o.getAuthority()));
    }

    @Test
    public void updatePasswordForChangePasswordPrivilegedUser() {
        User user = User.builder().id(ID).password(PASSWORD).build();
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

        passwordService.changePasswordForPrivilegedUser(PasswordCommand.builder()
                .password(PASSWORD).matchingPassword(PASSWORD).build());

        verify(userService).saveOrUpdateUser(user);
    }
}