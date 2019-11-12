package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.mappers.UserMapper;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import com.unmeshc.ourthoughts.services.exceptions.BadVerificationTokenException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.unmeshc.ourthoughts.TestLiterals.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    private UserMapper userMapper = UserMapper.INSTANCE;

    private RegistrationServiceImpl registrationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        registrationService = new RegistrationServiceImpl(roleRepository, userService,
                emailService, verificationTokenService, passwordEncoder, userMapper);
    }

    @Test(expected = RuntimeException.class)
    public void saveUserAndVerifyEmailRoleNotFound() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        registrationService.saveUserAndVerifyByEmailing(any(UserCommand.class),
                any(HttpServletRequest.class));
    }

    @Test
    public void saveUserAndVerifyEmail() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Role role = Role.builder().name("USER").build();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = User.builder().password(UNMESH).roles(roles).active(false).build();
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn(UNMESH);
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(userService.saveOrUpdateUser(any(User.class))).thenReturn(user);

        registrationService.saveUserAndVerifyByEmailing(UserCommand.builder().build(), request);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(emailService).sendAccountActivationLinkForUser(
                userArgumentCaptor.capture(), eq(request));
        User returnUser = userArgumentCaptor.getValue();

        assertThat(returnUser.getRoles().contains(role)).isTrue();
        assertThat(returnUser.getActive()).isFalse();
        assertThat(returnUser.getPassword()).isEqualTo(UNMESH);
    }

    @Test
    public void isUserEmailExistsTrue() {
        when(userService.isEmailExists(anyString())).thenReturn(true);
        assertThat(registrationService.isUserEmailExists(EMAIL)).isTrue();
        verify(userService).isEmailExists(EMAIL);
    }

    @Test
    public void isUserEmailExistsFalse() {
        when(userService.isEmailExists(anyString())).thenReturn(false);
        assertThat(registrationService.isUserEmailExists(EMAIL)).isFalse();
        verify(userService).isEmailExists(EMAIL);
    }

    @Test(expected = BadVerificationTokenException.class)
    public void activateUserByTokenNotFound() throws BadVerificationTokenException {
        when(verificationTokenService.getVerificationTokenByToken(anyString())).thenReturn(null);
        registrationService.activateUserByVerificationToken(TOKEN);
    }

    @Test(expected = BadVerificationTokenException.class)
    public void activateUserByTokenExpired() throws BadVerificationTokenException {
        VerificationToken verificationToken = VerificationToken.builder()
                .expiryDate(LocalDateTime.now().minusDays(2)).build();
        when(verificationTokenService.getVerificationTokenByToken(anyString())).thenReturn(verificationToken);

        registrationService.activateUserByVerificationToken(TOKEN);
    }

    @Test
    public void activateUserByToken() throws BadVerificationTokenException {
        VerificationToken verificationToken = VerificationToken.builder()
            .user(User.builder().build()).expiryDate(LocalDateTime.now().plusDays(1)).build();
        when(verificationTokenService.getVerificationTokenByToken(anyString()))
                .thenReturn(verificationToken);

        registrationService.activateUserByVerificationToken(TOKEN);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveOrUpdateUser(userArgumentCaptor.capture());
        User foundUser = userArgumentCaptor.getValue();

        assertThat(foundUser.getActive()).isTrue();
    }
}