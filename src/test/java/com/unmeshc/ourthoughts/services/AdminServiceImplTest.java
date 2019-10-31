package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AdminServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private AdminServiceImpl service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = RuntimeException.class)
    public void createAdminUserAdminRoleNotFound() throws Exception {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        service.createAdminUser();
    }

    @Test
    public void createAdminUser() {
        Role adminRole = Role.builder().name("ADMIN").id(1L).build();
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode(AdminService.ADMIN_PASSWORD))
                .thenReturn(AdminService.ADMIN_PASSWORD);

        service.createAdminUser();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveOrUpdate(userArgumentCaptor.capture());
        User user = userArgumentCaptor.getValue();

        assertThat(user.getEmail()).isEqualTo(AdminService.ADMIN_EMAIL);
        assertThat(user.getPassword()).isEqualTo(AdminService.ADMIN_PASSWORD);
        assertThat(user.getActive()).isTrue();
    }

    @Test
    public void isAdminExistsTrue() {
        when(userService.isEmailExists(anyString())).thenReturn(true);
        assertThat(service.isAdminExists()).isTrue();
    }

    @Test
    public void isAdminExistsFalse() {
        when(userService.isEmailExists(anyString())).thenReturn(false);
        assertThat(service.isAdminExists()).isFalse();
    }

    @Test
    public void getAllUsers() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<User> userPage = Mockito.mock(Page.class);
        when(userService.getAllExceptAdmin(anyString(), any(Pageable.class)))
                .thenReturn(userPage);

        Page<User> foundUserPage = service.getAllUsers(pageable);
        assertThat(foundUserPage).isEqualTo(userPage);

        verify(userService).getAllExceptAdmin(AdminService.ADMIN_EMAIL, pageable);
    }

    @Test
    public void getPostsByUser() {
        User user = User.builder().id(1L).email("unmesh@gmail.cm").build();
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Post> postPage = Mockito.mock(Page.class);
        when(postService.getPostsByUser(any(User.class), any(Pageable.class)))
                .thenReturn(postPage);

        Page<Post> foundPostPage = service.getPostsByUser(user, pageable);
        assertThat(foundPostPage).isEqualTo(postPage);
        verify(postService).getPostsByUser(user, pageable);
    }

    @Test
    public void getUserById() {
        User user = User.builder().id(1L).build();
        when(userService.getById(anyLong())).thenReturn(user);

        User foundUser = service.getUserById(1L);

        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    public void getPostById() {
        Post post = Post.builder().id(1L).build();
        when(postService.getById(anyLong())).thenReturn(post);

        Post foundPost = service.getPostById(1L);

        assertThat(foundPost).isEqualTo(post);
    }

    @Test
    public void getCommentsByPost() {
        Page<Comment> commentPage = Mockito.mock(Page.class);
        Post post = Post.builder().id(1L).build();
        Pageable pageable = Mockito.mock(Pageable.class);
        when(commentService.getCommentsByPost(any(Post.class), any(Pageable.class)))
                .thenReturn(commentPage);

        Page<Comment> foundCommentPage = service.getCommentsByPost(post, pageable);

        assertThat(foundCommentPage).isEqualTo(commentPage);
    }

    @Test
    public void deleteCommentById() {
        service.deleteCommentById(1L);
        verify(commentService).deleteById(1L);
    }

    @Test
    public void deletePostWithCommentsById() {
        Post post = Post.builder().id(1L).build();
        when(postService.getById(1L)).thenReturn(post);

        service.deletePostWithCommentsById(1L);

        verify(commentService).deleteByPost(post);
        verify(postService).delete(post);
    }

    @Test
    public void deleteUserWithPostById() {
        AdminServiceImpl spyService = Mockito.spy(service);
        User user = User.builder().id(1L).build();
        List<Post> posts = Arrays.asList(
            Post.builder().id(1L).build(),
            Post.builder().id(2L).build()
        );
        when(userService.getById(anyLong())).thenReturn(user);
        when(postService.getPostsByUser(any(User.class))).thenReturn(posts);

        spyService.deleteUserWithPostsById(1L);

        verify(userService).getById(1L);
        verify(postService).getPostsByUser(user);
        verify(spyService, times(2)).deletePostWithCommentsById(anyLong());
        verify(userService).delete(user);
    }

    @Test
    public void changeAdminPassword() {
        User user = User.builder().id(1L).password("password").build();
        when(userService.getByEmail(AdminService.ADMIN_EMAIL)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("password");

        service.changeAdminPassword("newPassword");

        verify(userService).saveOrUpdate(user);
    }

    @Test
    public void resetAdminPassword() {
        AdminServiceImpl spyService = Mockito.spy(service);
        User user = User.builder().id(1L).password("password").build();
        when(userService.getByEmail(AdminService.ADMIN_EMAIL)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("password");

        spyService.resetAdminPassword();

        verify(spyService).changeAdminPassword(AdminService.ADMIN_PASSWORD);
        verify(userService).saveOrUpdate(user);
    }
}