package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.dtos.PostCommentAdminDto;
import com.unmeshc.ourthoughts.dtos.UserAdminListDto;
import com.unmeshc.ourthoughts.dtos.UserPostAdminDto;
import com.unmeshc.ourthoughts.services.AdminService;
import com.unmeshc.ourthoughts.services.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

import static com.unmeshc.ourthoughts.TestLiterals.NEW_PASSWORD;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                  .setControllerAdvice(new ControllerExceptionHandler()).build();
    }

    @Test
    public void resetPassword() throws Exception {
        mockMvc.perform(get("/admin/reset/password"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(AdminController.REDIRECT_LOGIN));

        verify(adminService).resetAdminPassword();
    }

    @Test
    public void changePassword() throws Exception {
        mockMvc.perform(post("/admin/change/password")
                    .param("newPassword", NEW_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(AdminController.REDIRECT_LOGIN));

        verify(adminService).changeAdminPasswordAndLogout(eq(NEW_PASSWORD),
                any(HttpServletRequest.class));
    }

    @Test
    public void adminConsole() throws Exception {
        mockMvc.perform(get("/admin/console.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(AdminController.REDIRECT_ADMIN_ALL_USER));
    }

    @Test
    public void allUsersWithDefaultValues() throws Exception {
        when(adminService.getAllUsers(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(UserAdminListDto.builder().build());

        mockMvc.perform(get("/admin/all/user"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userAdminListDto"))
                .andExpect(view().name(AdminController.CONSOLE));

        verify(adminService).getAllUsers(0, 0, false);
    }

    @Test
    public void allUsersWithValues() throws Exception {
        when(adminService.getAllUsers(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(UserAdminListDto.builder().build());

        mockMvc.perform(get("/admin/all/user")
                    .param("page", "3")
                    .param("size", "5")
                    .param("delete", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userAdminListDto"))
                .andExpect(view().name(AdminController.CONSOLE));

        verify(adminService).getAllUsers(3, 5, true);
    }

    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(get("/admin/all/user/" + 1 + "/delete"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(AdminController.REDIRECT_ADMIN_ALL_USER_DELETE));

        verify(adminService).deleteUserWithPostsById(1);
    }

    @Test
    public void showUserPostsWithDefaultValuesUserNotFoundException() throws Exception {
        when(adminService.getPostsForUser(anyLong(), anyInt(), anyInt(), anyBoolean()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/admin/user/" + 1 + "/post"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void showUserPostsWithDefaultValues() throws Exception {
        when(adminService.getPostsForUser(anyLong(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(UserPostAdminDto.builder().build());

        mockMvc.perform(get("/admin/user/" + 1 + "/post"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userPostAdminDto"))
                .andExpect(view().name(AdminController.USER_POSTS));

        verify(adminService).getPostsForUser(1L, 0, 0, false);
    }

    @Test
    public void showUserPostsWithValues() throws Exception {
        when(adminService.getPostsForUser(anyLong(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(UserPostAdminDto.builder().build());

        mockMvc.perform(get("/admin/user/" + 1 + "/post")
                    .param("page", "3")
                    .param("size", "5")
                    .param("delete", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userPostAdminDto"))
                .andExpect(view().name(AdminController.USER_POSTS));

        verify(adminService).getPostsForUser(1L, 3, 5, true);
    }

    @Test
    public void deletePost() throws Exception {
        mockMvc.perform(get("/admin/user/" + 1 + "/post/" + 1 + "/delete"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(
                       "redirect:/admin/user/" + 1 + "/post?delete=true"));

        verify(adminService).deletePostWithCommentsById(1);
    }

    @Test
    public void showPostCommentsWithDefaultValuesPostNotFoundException() throws Exception {
        when(adminService.getCommentsForPost(anyLong(), anyInt(), anyInt(), anyBoolean()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/admin/post/" + 1 + "/comment"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void showPostCommentsWithDefaultValues() throws Exception {
        when(adminService.getCommentsForPost(anyLong(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(PostCommentAdminDto.builder().build());

        mockMvc.perform(get("/admin/post/" + 1 + "/comment"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postCommentAdminDto"))
                .andExpect(view().name(AdminController.POST_COMMENTS));

        verify(adminService).getCommentsForPost(1L, 0, 0, false);
    }

    @Test
    public void showPostCommentsWithValues() throws Exception {
        when(adminService.getCommentsForPost(anyLong(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(PostCommentAdminDto.builder().build());

        mockMvc.perform(get("/admin/post/" + 1 + "/comment")
                    .param("page", "3")
                    .param("size", "5")
                    .param("delete", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postCommentAdminDto"))
                .andExpect(view().name(AdminController.POST_COMMENTS));

        verify(adminService).getCommentsForPost(1L, 3, 5, true);
    }

    @Test
    public void deleteComment() throws Exception {
        mockMvc.perform(get("/admin/post/" + 1 + "/comment/" + 1 + "/delete"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name("redirect:/admin/post/" + 1 +
                       "/comment?delete=true"));

        verify(adminService).deleteCommentById(1);
    }
}