package com.unmeshc.ourthoughts.bootstrap;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.AdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/26/2019
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final AdminService adminService;

    public DataLoader(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void run(String... args) throws Exception {
        User adminUser = User.builder().firstName("Admin").lastName("Localhost")
                         .email("admin@localhost.com").password("admin").build();
        adminService.createAdminUser(adminUser);
    }
}
