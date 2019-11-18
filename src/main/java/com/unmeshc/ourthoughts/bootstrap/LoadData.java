package com.unmeshc.ourthoughts.bootstrap;

import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import com.unmeshc.ourthoughts.services.AdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/26/2019
 */
@Component
public class LoadData implements CommandLineRunner {

    private static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    private final AdminService adminService;
    private final RoleRepository roleRepository;

    public LoadData(AdminService adminService,
                    RoleRepository roleRepository) {
        this.adminService = adminService;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Role foundAdmin = roleRepository.findByName(ADMIN).orElse(null);
        if (foundAdmin == null) {
            Role admin = Role.builder().name(ADMIN).build();
            roleRepository.save(admin);
        }

        Role foundUser = roleRepository.findByName(USER).orElse(null);
        if (foundUser == null) {
            Role user = Role.builder().name(USER).build();
            roleRepository.save(user);
        }
        if (!adminService.isAdminExists()) {
            adminService.createAdminUser();
        }
    }
}
