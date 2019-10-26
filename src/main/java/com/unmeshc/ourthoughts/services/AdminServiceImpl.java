package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
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
    public void createAdminUser(User adminUser) {
        Role admin = roleRepository.findByName("ADMIN").orElse(null);
        if (admin == null) {
            log.error("Admin role not found");
            throw new RuntimeException("Admin role not found, try again.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(admin);

        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        adminUser.setRoles(roles);
        adminUser.setActive(true);

        userService.saveOrUpdateUser(adminUser);
    }
}
