package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by uc on 10/26/2019
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    public static final String ADMIN_EMAIL = "admin@localhost.com";
    static final String ADMIN_PASSWORD = "admin";

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserService userService,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createAdminUser() {
        Role admin = roleRepository.findByName("ADMIN").orElse(null);
        if (admin == null) {
            log.error("Admin role not found");
            throw new RuntimeException("Admin role not found, try again.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(admin);

        User adminUser = User.builder().email(ADMIN_EMAIL).active(true).roles(roles).
                password(passwordEncoder.encode(ADMIN_PASSWORD)).build();

        userService.saveOrUpdateUser(adminUser);
    }

    @Override
    public boolean isAdminExists() {
        return userService.isEmailExists(ADMIN_EMAIL);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {

        return userService.getAllUsers(ADMIN_EMAIL, pageable);
    }
}
