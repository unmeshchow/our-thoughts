package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Created by uc on 10/26/2019
 */
@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserPageTracker userPageTracker;
    private final AdminService adminService;
    private final ControllerUtils controllerUtils;

    public AdminController(UserPageTracker userPageTracker,
                           AdminService adminService,
                           ControllerUtils controllerUtils) {
        this.userPageTracker = userPageTracker;
        this.adminService = adminService;
        this.controllerUtils = controllerUtils;
    }

    @GetMapping("/console.html")
    public String adminConsole() {
        return "redirect:/admin/all/users";
    }

    @GetMapping("/all/users")
    public String allUsers(@RequestParam("page") Optional<Integer> page,
                           @RequestParam("size") Optional<Integer> size,
                           Model model) {

        int currentPage = page.orElse(userPageTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("registrationDateTime").descending()); // zero based page

        Page<User> userPage = adminService.getAllUsers(pageable);
        userPageTracker.setCurrentPage(userPage.getNumber() + 1);

        List<UserCommand> userCommands = controllerUtils.convertToUserCommandList(userPage.getContent());

        model.addAttribute("users", userCommands);
        model.addAttribute("currentPage", userPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", userPageTracker.getPageNumbersForPagination(userPage));

        return "admin/console";
    }

}
