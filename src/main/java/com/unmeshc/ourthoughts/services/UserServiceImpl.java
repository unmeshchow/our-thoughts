package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by uc on 10/15/2019
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User saveOrUpdate(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User getById(long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public Page<User> getAllExceptAdmin(String adminEmail, Pageable pageable) {
        return userRepository.findAllUserExceptAdmin(adminEmail, pageable);
    }
}
