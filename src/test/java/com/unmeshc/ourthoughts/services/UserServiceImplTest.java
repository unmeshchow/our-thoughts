package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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
    public void saveOrUpdate() {
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

    @Test
    public void getByIdNull() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThat(service.getById(1L)).isNull();
    }

    @Test
    public void getById() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User foundUser = service.getById(1L);

        assertThat(foundUser).isEqualTo(user);
        verify(userRepository).findById(1L);
    }

    @Test
    public void delete() {
        User user = User.builder().id(1L).build();
        service.delete(user);
        verify(userRepository).delete(user);
    }

    @Test
    public void getAllExceptAdmin() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<User> userPage = Mockito.mock(Page.class);
        when(userRepository.findAllUserExceptAdmin(anyString(), any(Pageable.class)))
                .thenReturn(userPage);

        Page<User> foundUserPost = service.getAllExceptAdmin(AdminService.ADMIN_EMAIL, pageable);

        assertThat(foundUserPost).isEqualTo(userPage);
        verify(userRepository).findAllUserExceptAdmin(AdminService.ADMIN_EMAIL, pageable);
    }
}