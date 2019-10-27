package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.PostAdminDto;
import com.unmeshc.ourthoughts.dtos.UserAdminDto;
import com.unmeshc.ourthoughts.dtos.UserPostAdminDto;
import com.unmeshc.ourthoughts.exceptions.NotFoundException;
import com.unmeshc.ourthoughts.services.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final PostPageAdminTracker postPageAdminTracker;

    public AdminController(UserPageTracker userPageTracker,
                           AdminService adminService,
                           ControllerUtils controllerUtils,
                           PostPageAdminTracker postPageAdminTracker) {
        this.userPageTracker = userPageTracker;
        this.adminService = adminService;
        this.controllerUtils = controllerUtils;
        this.postPageAdminTracker = postPageAdminTracker;
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
                Sort.by("lastName").ascending().and(Sort.by("firstName").descending())); // zero based page

        Page<User> userPage = adminService.getAllUsers(pageable);
        userPageTracker.setCurrentPage(userPage.getNumber() + 1);

        List<UserAdminDto> userAdminDtos =
                controllerUtils.convertToAdminUserDtoList(userPage.getContent());

        model.addAttribute("userAdminDtos", userAdminDtos);
        model.addAttribute("currentPage", userPageTracker.getCurrentPage());
        model.addAttribute("pageNumbers", userPageTracker.getPageNumbersForPagination(userPage));

        return "admin/console";
    }

    @GetMapping("/user/{userId}/post")
    public String showUserPosts(@RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size,
                                @PathVariable long userId,
                                Model model) {

        User user = adminService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id - " + userId);
        }

        int currentPage = page.orElse(postPageAdminTracker.getCurrentPage());
        int pageSize = size.orElse(2);

        Pageable pageable = PageRequest.of((currentPage - 1), pageSize,
                Sort.by("creationDateTime").descending()); // zero based page

        Page<Post> postPage = adminService.getPostForUser(user, pageable);
        postPageAdminTracker.setCurrentPage(postPage.getNumber() + 1);

        List<PostAdminDto> postAdminDtos =
                controllerUtils.convertToPostAdminDtoList(postPage.getContent());
        UserPostAdminDto userPostAdminDto = UserPostAdminDto.builder().id(userId).
                firstName(user.getFirstName()).postAdminDtos(postAdminDtos).build();

        model.addAttribute("userPostAdminDto", userPostAdminDto);
        model.addAttribute("currentPage", postPageAdminTracker.getCurrentPage());
        model.addAttribute("pageNumbers", postPageAdminTracker.getPageNumbersForPagination(postPage));

        return "admin/userPosts";
    }

}
