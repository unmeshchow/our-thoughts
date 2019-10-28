package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.UserRepository;
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

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void isEmailExistsTrue() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThat(service.isEmailExists(anyString())).isTrue();
    }

    @Test
    public void isEmailExistsFalse() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        assertThat(service.isEmailExists(anyString())).isFalse();
    }

    @Test
    public void saveOrUpdateUser() {
        User user = User.builder().email("unmesh@gmail.com").build();
        when(userRepository.save(user)).thenReturn(User.builder().id(1L).build());

        service.saveOrUpdate(user);
        verify(userRepository).save(user);
    }

    @Test
    public void getByEmailNull() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        User user = service.getByEmail("unmesh@gamil.com");

        assertThat(user).isNull();
        verify(userRepository).findByEmail("unmesh@gamil.com");
    }

    @Test
    public void getByEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(
                Optional.of(User.builder().id(1L).email("unmesh@gamil.com").build()));

        User user = service.getByEmail("unmesh@gamil.com");

        assertThat(user).isNotNull();
        verify(userRepository).findByEmail("unmesh@gamil.com");
    }
}