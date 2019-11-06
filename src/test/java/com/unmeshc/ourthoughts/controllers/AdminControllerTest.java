package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.controllers.pagination.AdminCommentPageTracker;
import com.unmeshc.ourthoughts.controllers.pagination.AdminPostPageTracker;
import com.unmeshc.ourthoughts.controllers.pagination.AdminUserPageTracker;
import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.CommentAdminDto;
import com.unmeshc.ourthoughts.dtos.PostAdminDto;
import com.unmeshc.ourthoughts.dtos.UserAdminDto;
import com.unmeshc.ourthoughts.services.AdminService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private ControllerUtils controllerUtils;

    @Mock
    private AdminPostPageTracker adminPostPageTracker;

    @Mock
    private AdminCommentPageTracker adminCommentPageTracker;

    @Mock
    private AdminUserPageTracker adminUserPageTracker;

    @InjectMocks
    private AdminController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
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
                    .param("newPassword", "newPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(AdminController.REDIRECT_LOGIN));

        verify(adminService).changeAdminPassword("newPassword");
    }

    @Test
    public void changePasswordWithoutMockMvc() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        String viewName = controller.changePassword("newPassword", request);

        assertThat(viewName).isEqualTo(AdminController.REDIRECT_LOGIN);
        verify(adminService).changeAdminPassword("newPassword");
        verify(request).logout();
    }

    @Test
    public void adminConsole() throws Exception {
        mockMvc.perform(get("/admin/console.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(AdminController.REDIRECT_ADMIN_ALL_USER));
    }

    @Test
    public void allUsersWithDefaultValues() throws Exception {
        List<UserAdminDto> userAdminDtos = new ArrayList<>();
        Page<User> userPage = Mockito.mock(Page.class);
        when(adminUserPageTracker.getCurrentPage()).thenReturn(1);
        when(adminService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
        when(controllerUtils.convertToAdminUserDtoList(userPage.getContent()))
                .thenReturn(userAdminDtos);

        mockMvc.perform(get("/admin/all/user"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userAdminDtos", hasSize(0)))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(view().name(AdminController.CONSOLE));

        ArgumentCaptor<Pageable> pageableArgumentCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(adminService).getAllUsers(pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(2);
        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("lastName")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("firstName")).isNotNull();

        verify(adminUserPageTracker).setCurrentPage(anyInt());
        verify(controllerUtils).convertToAdminUserDtoList(userPage.getContent());
        verify(adminUserPageTracker, times(2)).getCurrentPage();
        verify(adminUserPageTracker).getPageNumbersForPagination(userPage);
    }

    @Test
    public void allUsersWithValues() throws Exception {
        List<UserAdminDto> userAdminDtos = new ArrayList<>();
        Page<User> userPage = Mockito.mock(Page.class);
        when(adminService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
        when(controllerUtils.convertToAdminUserDtoList(userPage.getContent()))
                .thenReturn(userAdminDtos);

        mockMvc.perform(get("/admin/all/user")
                    .param("page", "3")
                    .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userAdminDtos", hasSize(0)))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(view().name(AdminController.CONSOLE));

        ArgumentCaptor<Pageable> pageableArgumentCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(adminService).getAllUsers(pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("lastName")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("firstName")).isNotNull();

        verify(adminUserPageTracker).setCurrentPage(anyInt());
        verify(controllerUtils).convertToAdminUserDtoList(userPage.getContent());
        verify(adminUserPageTracker, times(1)).getCurrentPage();
        verify(adminUserPageTracker).getPageNumbersForPagination(userPage);
    }

    @Test
    public void allUsersWithLastPageProblem() throws Exception {
        List<UserAdminDto> userAdminDtos = new ArrayList<>();
        Page<User> userPage = Mockito.mock(Page.class);
        when(adminService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
        when(controllerUtils.convertToAdminUserDtoList(userPage.getContent()))
                .thenReturn(userAdminDtos);
        when(userPage.getContent()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/all/user")
                    .param("page", "3")
                    .param("size", "5")
                    .param("delete", "yes"))
                .andExpect(status().isOk())
                .andExpect(view().name(AdminController.CONSOLE));

        verify(adminService, times(2)).getAllUsers(any(Pageable.class));
    }

    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(get("/admin/all/user/" + 1 + "/delete"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(AdminController.REDIRECT_ADMIN_ALL_USER_DELETE));

        verify(adminService).deleteUserWithPostsById(1);
    }

    @Test
    public void showUserPostsWithDefaultValuesNotFoundException() throws Exception {
        when(adminService.getUserById(anyInt())).thenReturn(null);

        mockMvc.perform(get("/admin/user/" + 1 + "/post"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void showUserPostsWithDefaultValues() throws Exception {
        User user = User.builder().id(1L).firstName("Unmesh").build();
        List<PostAdminDto> postAdminDtos = new ArrayList<>();
        Page<Post> postPage = Mockito.mock(Page.class);
        when(adminService.getUserById(anyLong())).thenReturn(user);
        when(adminPostPageTracker.getUserId()).thenReturn(2L);
        when(adminPostPageTracker.getCurrentPage()).thenReturn(1);
        when(adminService.getPostsByUser(any(User.class), any(Pageable.class)))
                .thenReturn(postPage);
        when(controllerUtils.convertToPostAdminDtoList(postPage.getContent()))
                .thenReturn(postAdminDtos);

        mockMvc.perform(get("/admin/user/" + 1 + "/post"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userPostAdminDto"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(view().name(AdminController.USER_POSTS));

        ArgumentCaptor<Pageable> pageableArgumentCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(adminService).getPostsByUser(eq(user), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(2);
        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("creationDateTime")).isNotNull();

        verify(adminPostPageTracker).reset();
        verify(adminPostPageTracker).setCurrentPage(anyInt());
        verify(adminPostPageTracker).setUserId(anyLong());
        verify(controllerUtils).convertToPostAdminDtoList(postPage.getContent());
        verify(adminPostPageTracker, times(2)).getCurrentPage();
        verify(adminPostPageTracker).getPageNumbersForPagination(postPage);
    }

    @Test
    public void showUserPostsWithValues() throws Exception {
        User user = User.builder().id(1L).firstName("Unmesh").build();
        List<PostAdminDto> postAdminDtos = new ArrayList<>();
        Page<Post> postPage = Mockito.mock(Page.class);
        when(adminService.getUserById(anyLong())).thenReturn(user);
        when(adminPostPageTracker.getUserId()).thenReturn(1L);
        when(adminService.getPostsByUser(any(User.class), any(Pageable.class)))
                .thenReturn(postPage);
        when(controllerUtils.convertToPostAdminDtoList(postPage.getContent()))
                .thenReturn(postAdminDtos);

        mockMvc.perform(get("/admin/user/" + 1 + "/post")
                    .param("page", "3")
                    .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userPostAdminDto"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(view().name(AdminController.USER_POSTS));

        ArgumentCaptor<Pageable> pageableArgumentCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(adminService).getPostsByUser(eq(user), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("creationDateTime")).isNotNull();

        verify(adminPostPageTracker).setCurrentPage(anyInt());
        verify(adminPostPageTracker).setUserId(anyLong());
        verify(controllerUtils).convertToPostAdminDtoList(postPage.getContent());
        verify(adminPostPageTracker, times(1)).getCurrentPage();
        verify(adminPostPageTracker).getPageNumbersForPagination(postPage);
    }

    @Test
    public void showUserPostsWithLastPageProblem() throws Exception {
        User user = User.builder().id(1L).firstName("Unmesh").build();
        List<PostAdminDto> postAdminDtos = new ArrayList<>();
        Page<Post> postPage = Mockito.mock(Page.class);
        when(postPage.getContent()).thenReturn(new ArrayList<>());
        when(adminService.getUserById(anyLong())).thenReturn(user);
        when(adminPostPageTracker.getUserId()).thenReturn(1L);
        when(adminService.getPostsByUser(any(User.class), any(Pageable.class)))
                .thenReturn(postPage);
        when(controllerUtils.convertToPostAdminDtoList(postPage.getContent()))
                .thenReturn(postAdminDtos);

        mockMvc.perform(get("/admin/user/" + 1 + "/post")
                    .param("page", "3")
                    .param("size", "5")
                    .param("delete", "yes"))
                .andExpect(status().isOk())
                .andExpect(view().name(AdminController.USER_POSTS));

        verify(adminService, times(2)).getPostsByUser(eq(user), any(Pageable.class));
    }

    @Test
    public void deletePost() throws Exception {
        mockMvc.perform(get("/admin/user/" + 1 + "/post/" + 1 + "/delete"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(
                       "redirect:/admin/user/" + 1 + "/post?delete=yes"));

        verify(adminService).deletePostWithCommentsById(1);
    }

    @Test
    public void showPostCommentsWithDefaultValuesNotFoundException() throws Exception {
        when(adminService.getPostById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/admin/post/" + 1 + "/comment"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void showPostCommentsWithDefaultValues() throws Exception {
        User user = User.builder().id(1L).build();
        Post post = Post.builder().id(1L).user(user).build();
        List<CommentAdminDto> commentAdminDtos = new ArrayList<>();
        Page<Comment> commentPage = Mockito.mock(Page.class);
        when(adminService.getPostById(anyLong())).thenReturn(post);
        when(adminCommentPageTracker.getPostId()).thenReturn(2L);
        when(adminCommentPageTracker.getCurrentPage()).thenReturn(1);
        when(adminService.getCommentsByPost(any(Post.class), any(Pageable.class)))
                .thenReturn(commentPage);
        when(controllerUtils.convertToCommentAdminDtoList(commentPage.getContent()))
                .thenReturn(commentAdminDtos);

        mockMvc.perform(get("/admin/post/" + 1 + "/comment"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postCommentAdminDto"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(view().name(AdminController.POST_COMMENTS));

        ArgumentCaptor<Pageable> pageableArgumentCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(adminService).getCommentsByPost(eq(post), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(2);
        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("addingDateTime")).isNotNull();

        verify(adminCommentPageTracker).reset();
        verify(adminCommentPageTracker).setCurrentPage(anyInt());
        verify(adminCommentPageTracker).setPostId(anyLong());
        verify(controllerUtils).convertToCommentAdminDtoList(commentPage.getContent());
        verify(adminCommentPageTracker, times(2)).getCurrentPage();
        verify(adminCommentPageTracker).getPageNumbersForPagination(commentPage);
    }

    @Test
    public void showPostCommentsWithValues() throws Exception {
        User user = User.builder().id(1L).build();
        Post post = Post.builder().id(1L).user(user).build();
        List<CommentAdminDto> commentAdminDtos = new ArrayList<>();
        Page<Comment> commentPage = Mockito.mock(Page.class);
        when(adminService.getPostById(anyLong())).thenReturn(post);
        when(adminCommentPageTracker.getPostId()).thenReturn(1L);
        when(adminService.getCommentsByPost(any(Post.class), any(Pageable.class)))
                .thenReturn(commentPage);
        when(controllerUtils.convertToCommentAdminDtoList(commentPage.getContent()))
                .thenReturn(commentAdminDtos);

        mockMvc.perform(get("/admin/post/" + 1 + "/comment")
                    .param("page", "3")
                    .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postCommentAdminDto"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(view().name(AdminController.POST_COMMENTS));

        ArgumentCaptor<Pageable> pageableArgumentCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(adminService).getCommentsByPost(eq(post), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("addingDateTime")).isNotNull();

        verify(adminCommentPageTracker).setCurrentPage(anyInt());
        verify(adminCommentPageTracker).setPostId(anyLong());
        verify(controllerUtils).convertToCommentAdminDtoList(commentPage.getContent());
        verify(adminCommentPageTracker, times(1)).getCurrentPage();
        verify(adminCommentPageTracker).getPageNumbersForPagination(commentPage);
    }

    @Test
    public void showPostCommentsWithLastPageProblem() throws Exception {
        User user = User.builder().id(1L).build();
        Post post = Post.builder().id(1L).user(user).build();
        List<CommentAdminDto> commentAdminDtos = new ArrayList<>();
        Page<Comment> commentPage = Mockito.mock(Page.class);
        when(commentPage.getContent()).thenReturn(new ArrayList<>());
        when(adminService.getPostById(anyLong())).thenReturn(post);
        when(adminCommentPageTracker.getPostId()).thenReturn(1L);
        when(adminService.getCommentsByPost(any(Post.class), any(Pageable.class)))
                .thenReturn(commentPage);
        when(controllerUtils.convertToCommentAdminDtoList(commentPage.getContent()))
                .thenReturn(commentAdminDtos);

        mockMvc.perform(get("/admin/post/" + 1 + "/comment")
                    .param("page", "3")
                    .param("size", "5")
                    .param("delete", "yes"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postCommentAdminDto"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(view().name(AdminController.POST_COMMENTS));

        verify(adminService, times(2)).getCommentsByPost(eq(post), any(Pageable.class));
    }

    @Test
    public void deleteComment() throws Exception {
        mockMvc.perform(get("/admin/post/" + 1 + "/comment/" + 1 + "/delete"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name("redirect:/admin/post/" + 1 + "/comment?delete=yes"));

        verify(adminService).deleteCommentById(1);
    }
}