package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.mappers.UserMapper;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import com.unmeshc.ourthoughts.services.exceptions.BadVerificationTokenException;
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

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public RegistrationServiceImpl(RoleRepository roleRepository,
                                   UserService userService,
                                   EmailService emailService,
                                   VerificationTokenService verificationTokenService,
                                   PasswordEncoder passwordEncoder,
                                   UserMapper userMapper) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public void saveUserAndVerifyByEmailing(UserCommand userCommand, HttpServletRequest request) {
        Role role = roleRepository.findByName("USER").orElse(null);

        if (role == null) {
            log.error("User role not found");
            throw new RuntimeException("User role not found, try again.");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = userMapper.userCommandToUser(userCommand);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(false);
        user.setRoles(roles);

        User savedUser = userService.saveOrUpdateUser(user);
        emailService.sendAccountActivationLinkForUser(savedUser, request);
    }

    @Override
    public boolean isUserEmailExists(String email) {
        return userService.isEmailExists(email);
    }

    @Override
    public void activateUserByVerificationToken(String token) throws BadVerificationTokenException {
        VerificationToken foundToken = verificationTokenService.getVerificationTokenByToken(token);
        if (foundToken == null || foundToken.isExpired()) {
            throw new BadVerificationTokenException();
        }

        User user = foundToken.getUser();
        user.setActive(true);

        userService.saveOrUpdateUser(user);
    }
}
