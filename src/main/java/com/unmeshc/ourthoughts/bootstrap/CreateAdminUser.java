package com.unmeshc.ourthoughts.bootstrap;

import com.unmeshc.ourthoughts.services.AdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by uc on 10/26/2019
 */
@Component
public class CreateAdminUser implements CommandLineRunner {

    private final AdminService adminService;

    public CreateAdminUser(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!adminService.isAdminExists()) {
            adminService.createAdminUser();
        }
    }
}
