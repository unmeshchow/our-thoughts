package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.converters.UserCommandToUser;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private UserCommandToUser userCommandToUser;

    @InjectMocks
    private RegistrationServiceImpl service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void activateUser() {
        User user = User.builder().email("unmesh@gmail.com").active(true).build();
        when(userService.saveOrUpdate(any())).thenReturn(User.builder().build());

        service.activateUser(User.builder().email("unmesh@gmail.com").build());

        verify(userService).saveOrUpdate(user);
    }

    @Test(expected = RuntimeException.class)
    public void saveAndVerifyUserException() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        service.saveUserAndVerifyEmail(any(UserCommand.class), any(HttpServletRequest.class));
    }

    @Test
    public void saveAndVerifyUser() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Role role = Role.builder().name("USER").build();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = User.builder().password("unmesh").roles(roles).active(false).build();

        when(userCommandToUser.convert(any(UserCommand.class))).thenReturn(
                User.builder().password("unmesh").build());
        when(passwordEncoder.encode(anyString())).thenReturn("unmesh");
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(userService.saveOrUpdate(user)).thenReturn(user);

        User savedUser = service.saveUserAndVerifyEmail(UserCommand.builder().build(), request);

        assertThat(savedUser).isNotNull();
        verify(userCommandToUser).convert(any(UserCommand.class));
        verify(passwordEncoder).encode(anyString());
        verify(roleRepository).findByName(anyString());
        verify(userService).saveOrUpdate(user);
        verify(emailService).sendAccountActivationLinkForUser(user, request);
    }

    @Test
    public void isUserEmailExistsTrue() {
        when(userService.isEmailExists(anyString())).thenReturn(true);
        assertThat(service.isUserEmailExists("unmesh@gmail.com")).isTrue();
        verify(userService).isEmailExists("unmesh@gmail.com");
    }

    @Test
    public void isUserEmailExistsFalse() {
        when(userService.isEmailExists(anyString())).thenReturn(false);
        assertThat(service.isUserEmailExists("unmesh@gmail.com")).isFalse();
        verify(userService).isEmailExists("unmesh@gmail.com");
    }

    @Test
    public void getVerificationTokenByToken() {
        String token = "token";
        VerificationToken verificationToken = VerificationToken.builder().id(1L).build();
        when(verificationTokenService.getByToken(anyString())).thenReturn(verificationToken);

        VerificationToken foundVerificationToken = service.getVerificationTokenByToken(token);
        assertThat(foundVerificationToken).isEqualTo(verificationToken);
        verify(verificationTokenService).getByToken(token);
    }
}