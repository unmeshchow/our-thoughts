package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.converters.UserCommandToUser;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import com.unmeshc.ourthoughts.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by uc on 10/15/2019
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserCommandToUser userCommandToUser;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserCommandToUser userCommandToUser,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userCommandToUser = userCommandToUser;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User saveUser(UserCommand userCommand) {
        Role role = roleRepository.findByName("USER").orElse(null);
        if (role == null) {
            log.error("User role not found");
            throw new RuntimeException();
        }
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = userCommandToUser.convert(userCommand);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setRoles(roles);

        return userRepository.save(user);
    }
}
