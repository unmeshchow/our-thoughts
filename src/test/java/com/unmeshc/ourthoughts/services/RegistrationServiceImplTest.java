package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.converters.UserCommandToUser;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
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
        when(userService.saveOrUpdateUser(any())).thenReturn(User.builder().build());

        service.activateUser(User.builder().email("unmesh@gmail.com").build());

        verify(userService).saveOrUpdateUser(user);
    }

    @Test(expected = RuntimeException.class)
    public void saveAndVerifyUserException() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        service.saveAndVerifyUser(any(UserCommand.class), any(HttpServletRequest.class));
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
        when(userService.saveOrUpdateUser(user)).thenReturn(user);

        User savedUser = service.saveAndVerifyUser(UserCommand.builder().build(), request);

        assertThat(savedUser).isNotNull();
        verify(userCommandToUser).convert(any(UserCommand.class));
        verify(passwordEncoder).encode(anyString());
        verify(roleRepository).findByName(anyString());
        verify(userService).saveOrUpdateUser(user);
        verify(emailService).sendAccountActivationLink(user, request);
    }
}