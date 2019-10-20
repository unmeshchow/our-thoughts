package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.converters.UserCommandToUser;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by uc on 10/16/2019
 */
@Slf4j
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserCommandToUser userCommandToUser;

    public RegistrationServiceImpl(UserService userService,
                                   EmailService emailService,
                                   PasswordEncoder passwordEncoder,
                                   RoleRepository roleRepository,
                                   UserCommandToUser userCommandToUser) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userCommandToUser = userCommandToUser;
    }

    @Override
    public User activateUser(User user) {
        user.setActive(true);
        return userService.saveOrUpdateUser(user);
    }

    @Override
    @Transactional
    public User saveAndVerifyUser(UserCommand userCommand, HttpServletRequest request) {
        Role role = roleRepository.findByName("USER").orElse(null);
        if (role == null) {
            log.error("User role not found");
            throw new RuntimeException();
        }
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = userCommandToUser.convert(userCommand);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(false);
        user.setRoles(roles);

        User savedUser = userService.saveOrUpdateUser(user);
        emailService.sendAccountActivationLink(savedUser, request);

        return savedUser;
    }
}
