package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.Comment;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.Role;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.PostCommentAdminDto;
import com.unmeshc.ourthoughts.dtos.UserAdminListDto;
import com.unmeshc.ourthoughts.dtos.UserPostAdminDto;
import com.unmeshc.ourthoughts.mappers.CommentMapper;
import com.unmeshc.ourthoughts.mappers.PostMapper;
import com.unmeshc.ourthoughts.mappers.UserMapper;
import com.unmeshc.ourthoughts.repositories.RoleRepository;
import com.unmeshc.ourthoughts.services.pagination.AdminCommentPageTracker;
import com.unmeshc.ourthoughts.services.pagination.AdminPostPageTracker;
import com.unmeshc.ourthoughts.services.pagination.AdminUserPageTracker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.unmeshc.ourthoughts.TestLiterals.*;
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

    @Mock
    private AdminPostPageTracker adminPostPageTracker;

    @Mock
    private AdminCommentPageTracker adminCommentPageTracker;

    @Mock
    private AdminUserPageTracker adminUserPageTracker;

    private UserMapper userMapper = UserMapper.INSTANCE;
    private PostMapper postMapper = PostMapper.INSTANCE;
    private CommentMapper commentMapper = CommentMapper.INSTANCE;

    private AdminServiceImpl adminService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        adminService = new AdminServiceImpl(roleRepository, userService, postService,
                commentService, passwordEncoder, adminPostPageTracker, adminCommentPageTracker,
                adminUserPageTracker, userMapper, postMapper, commentMapper);
    }


    @Test(expected = RuntimeException.class)
    public void createAdminUserAdminRoleNotFound() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        adminService.createAdminUser();
    }

    @Test
    public void createAdminUser() {
        Role adminRole = Role.builder().name("ADMIN").id(ID).build();
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode(AdminService.ADMIN_PASSWORD))
                .thenReturn(AdminService.ADMIN_PASSWORD);

        adminService.createAdminUser();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveOrUpdateUser(userArgumentCaptor.capture());
        User user = userArgumentCaptor.getValue();

        assertThat(user.getEmail()).isEqualTo(AdminService.ADMIN_EMAIL);
        assertThat(user.getPassword()).isEqualTo(AdminService.ADMIN_PASSWORD);
        assertThat(user.getActive()).isTrue();
    }

    @Test
    public void isAdminExistsTrue() {
        when(userService.isEmailExists(anyString())).thenReturn(true);
        assertThat(adminService.isAdminExists()).isTrue();
    }

    @Test
    public void isAdminExistsFalse() {
        when(userService.isEmailExists(anyString())).thenReturn(false);
        assertThat(adminService.isAdminExists()).isFalse();
    }

    @Test
    public void getAllUsersWithDefaultValues() { // page = 0, size = 0, deletePost = false
        List<User> users = Arrays.asList(
                User.builder().build(), User.builder().build()
        );
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        when(adminUserPageTracker.getCurrentPage()).thenReturn(1);
        Page<User> userPage = Mockito.mock(Page.class);
        when(userPage.stream()).thenReturn(users.stream());
        when(userService.getAllUsersExceptAdminAndInactive(anyString(), any(Pageable.class)))
                .thenReturn(userPage);
        when(adminUserPageTracker.getPageNumbersForPagination(userPage)).thenReturn(pageNumbers);

        UserAdminListDto userAdminListDto = adminService.getAllUsers(0, 0, false);
        assertThat(userAdminListDto.getUserAdminDtos().size()).isEqualTo(2);
        assertThat(userAdminListDto.getCurrentPage()).isEqualTo(1);
        assertThat(userAdminListDto.getPageNumbers()).isEqualTo(pageNumbers);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService).getAllUsersExceptAdminAndInactive(anyString(),
                pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("lastName")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("firstName")).isNotNull();

        verify(adminUserPageTracker, times(2)).getCurrentPage();
        verify(adminUserPageTracker).setCurrentPage(1);
    }

    @Test
    public void getAllUsersWithValues() {
        List<User> users = Arrays.asList(
                User.builder().build(), User.builder().build()
        );
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        when(adminUserPageTracker.getCurrentPage()).thenReturn(2);
        Page<User> userPage = Mockito.mock(Page.class);
        when(userPage.stream()).thenReturn(users.stream());
        when(userService.getAllUsersExceptAdminAndInactive(anyString(), any(Pageable.class)))
                .thenReturn(userPage);
        when(adminUserPageTracker.getPageNumbersForPagination(userPage)).thenReturn(pageNumbers);

        UserAdminListDto userAdminListDto = adminService.getAllUsers(2, 5, false);

        assertThat(userAdminListDto.getUserAdminDtos().size()).isEqualTo(2);
        assertThat(userAdminListDto.getCurrentPage()).isEqualTo(2);
        assertThat(userAdminListDto.getPageNumbers()).isEqualTo(pageNumbers);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService).getAllUsersExceptAdminAndInactive(anyString(),
                pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("lastName")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("firstName")).isNotNull();

        verify(adminUserPageTracker).getCurrentPage();
        verify(adminUserPageTracker).setCurrentPage(1);
    }

    @Test
    public void getAllUsersWithLastPageProblem() {
        List<User> users = Arrays.asList(
                User.builder().build(), User.builder().build()
        );
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        when(adminUserPageTracker.getCurrentPage()).thenReturn(2);
        Page<User> userPage = Mockito.mock(Page.class);
        when(userPage.getContent()).thenReturn(new ArrayList<>());
        when(userPage.stream()).thenReturn(users.stream());
        when(userService.getAllUsersExceptAdminAndInactive(anyString(), any(Pageable.class)))
                .thenReturn(userPage);
        when(adminUserPageTracker.getPageNumbersForPagination(userPage)).thenReturn(pageNumbers);

        UserAdminListDto userAdminListDto = adminService.getAllUsers(2, 5, true);

        assertThat(userAdminListDto.getUserAdminDtos().size()).isEqualTo(2);
        assertThat(userAdminListDto.getCurrentPage()).isEqualTo(2);
        assertThat(userAdminListDto.getPageNumbers()).isEqualTo(pageNumbers);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService, times(2)).getAllUsersExceptAdminAndInactive(anyString(),
                pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("lastName")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("firstName")).isNotNull();

        verify(adminUserPageTracker).getCurrentPage();
        verify(adminUserPageTracker).setCurrentPage(1);
    }

    @Test
    public void getPostsByUserWithDefaultValues() { // page = 0, size = 0, deletePost = false
        List<Post> posts = Arrays.asList(
                Post.builder().build(), Post.builder().build()
        );
        Page<Post> postPage = Mockito.mock(Page.class);
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        when(userService.getUserById(anyLong()))
                .thenReturn(User.builder().id(ID).firstName(FIRST_NAME).build());
        when(adminPostPageTracker.getUserId()).thenReturn(2L);
        when(adminPostPageTracker.getCurrentPage()).thenReturn(1);
        when(postPage.stream()).thenReturn(posts.stream());
        when(postService.getPostsByUser(any(User.class), any(Pageable.class))).thenReturn(postPage);
        when(adminPostPageTracker.getPageNumbersForPagination(postPage)).thenReturn(pageNumbers);

        UserPostAdminDto userPostAdminDto =
                adminService.getPostsForUser(ID, 0, 0, false);

        assertThat(userPostAdminDto.getId()).isEqualTo(ID);
        assertThat(userPostAdminDto.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(userPostAdminDto.getPostAdminDtos().size()).isEqualTo(2);
        assertThat(userPostAdminDto.getCurrentPage()).isEqualTo(1);
        assertThat(userPostAdminDto.getPageNumbers()).isEqualTo(pageNumbers);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(postService).getPostsByUser(any(User.class), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("creationDateTime")).isNotNull();

        verify(adminPostPageTracker, times(2)).getCurrentPage();
        verify(adminPostPageTracker).setCurrentPage(1);
        verify(adminPostPageTracker).setUserId(ID);
        verify(adminPostPageTracker).reset();
    }

    @Test
    public void getPostsByUserWithValues() {
        List<Post> posts = Arrays.asList(
                Post.builder().build(), Post.builder().build()
        );
        Page<Post> postPage = Mockito.mock(Page.class);
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        when(userService.getUserById(anyLong()))
                .thenReturn(User.builder().id(ID).firstName(FIRST_NAME).build());
        when(adminPostPageTracker.getUserId()).thenReturn(ID);
        when(adminPostPageTracker.getCurrentPage()).thenReturn(1);
        when(postPage.stream()).thenReturn(posts.stream());
        when(postService.getPostsByUser(any(User.class), any(Pageable.class)))
                .thenReturn(postPage);
        when(adminPostPageTracker.getPageNumbersForPagination(postPage)).thenReturn(pageNumbers);

        UserPostAdminDto userPostAdminDto =
                adminService.getPostsForUser(ID, 2, 5, false);

        assertThat(userPostAdminDto.getId()).isEqualTo(ID);
        assertThat(userPostAdminDto.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(userPostAdminDto.getPostAdminDtos().size()).isEqualTo(2);
        assertThat(userPostAdminDto.getCurrentPage()).isEqualTo(1);
        assertThat(userPostAdminDto.getPageNumbers()).isEqualTo(pageNumbers);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(postService).getPostsByUser(any(User.class), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("creationDateTime")).isNotNull();

        verify(adminPostPageTracker).getCurrentPage();
        verify(adminPostPageTracker).setCurrentPage(1);
        verify(adminPostPageTracker).setUserId(ID);
    }

    @Test
    public void getPostsByUserWithLastPageProblem() {
        List<Post> posts = Arrays.asList(
                Post.builder().build(), Post.builder().build()
        );
        Page<Post> postPage = Mockito.mock(Page.class);
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        when(postPage.getContent()).thenReturn(new ArrayList<>());
        when(userService.getUserById(anyLong()))
                .thenReturn(User.builder().id(ID).firstName(FIRST_NAME).build());
        when(adminPostPageTracker.getUserId()).thenReturn(ID);
        when(adminPostPageTracker.getCurrentPage()).thenReturn(1);
        when(postPage.stream()).thenReturn(posts.stream());
        when(postService.getPostsByUser(any(User.class), any(Pageable.class))).thenReturn(postPage);
        when(adminPostPageTracker.getPageNumbersForPagination(postPage)).thenReturn(pageNumbers);

        UserPostAdminDto userPostAdminDto =
                adminService.getPostsForUser(ID, 2, 5, true);

        assertThat(userPostAdminDto.getId()).isEqualTo(ID);
        assertThat(userPostAdminDto.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(userPostAdminDto.getPostAdminDtos().size()).isEqualTo(2);
        assertThat(userPostAdminDto.getCurrentPage()).isEqualTo(1);
        assertThat(userPostAdminDto.getPageNumbers()).isEqualTo(pageNumbers);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(postService, times(2))
                .getPostsByUser(any(User.class), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("creationDateTime")).isNotNull();

        verify(adminPostPageTracker).getCurrentPage();
        verify(adminPostPageTracker).setCurrentPage(1);
        verify(adminPostPageTracker).setUserId(ID);
    }

    @Test
    public void getCommentsByPostWithDefaultValues() { // page = 0, size = 0, deletePost = false
        List<Comment> comments = Arrays.asList(
                Comment.builder().build(), Comment.builder().build()
        );
        Page<Comment> commentPage = Mockito.mock(Page.class);
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        when(postService.getPostById(anyLong())).thenReturn(Post.builder().id(ID).title(TITLE)
                .user(User.builder().id(ID).build()).build());
        when(adminCommentPageTracker.getPostId()).thenReturn(2L);
        when(adminCommentPageTracker.getCurrentPage()).thenReturn(1);
        when(commentPage.stream()).thenReturn(comments.stream());
        when(commentService.getCommentsByPost(any(Post.class), any(Pageable.class)))
                .thenReturn(commentPage);
        when(adminCommentPageTracker.getPageNumbersForPagination(commentPage))
                .thenReturn(pageNumbers);

        PostCommentAdminDto postCommentAdminDto =
                adminService.getCommentsForPost(ID, 0, 0, false);

        assertThat(postCommentAdminDto.getId()).isEqualTo(ID);
        assertThat(postCommentAdminDto.getTitle()).isEqualTo(TITLE);
        assertThat(postCommentAdminDto.getCommentAdminDtos().size()).isEqualTo(2);
        assertThat(postCommentAdminDto.getCurrentPage()).isEqualTo(1);
        assertThat(postCommentAdminDto.getPageNumbers()).isEqualTo(pageNumbers);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(commentService).getCommentsByPost(any(Post.class), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("addingDateTime")).isNotNull();

        verify(adminCommentPageTracker, times(2)).getCurrentPage();
        verify(adminCommentPageTracker).setCurrentPage(1);
        verify(adminCommentPageTracker).setPostId(ID);
        verify(adminCommentPageTracker).reset();
    }

    @Test
    public void getCommentsByPostWithValues() {
        List<Comment> comments = Arrays.asList(
                Comment.builder().build(), Comment.builder().build()
        );
        Page<Comment> commentPage = Mockito.mock(Page.class);
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        when(postService.getPostById(anyLong())).thenReturn(Post.builder().id(ID).title(TITLE)
                .user(User.builder().id(ID).build()).build());
        when(adminCommentPageTracker.getPostId()).thenReturn(ID);
        when(adminCommentPageTracker.getCurrentPage()).thenReturn(1);
        when(commentPage.stream()).thenReturn(comments.stream());
        when(commentService.getCommentsByPost(any(Post.class), any(Pageable.class)))
                .thenReturn(commentPage);
        when(adminCommentPageTracker.getPageNumbersForPagination(commentPage))
                .thenReturn(pageNumbers);

        PostCommentAdminDto postCommentAdminDto =
                adminService.getCommentsForPost(ID, 2, 5, false);

        assertThat(postCommentAdminDto.getId()).isEqualTo(ID);
        assertThat(postCommentAdminDto.getTitle()).isEqualTo(TITLE);
        assertThat(postCommentAdminDto.getCommentAdminDtos().size()).isEqualTo(2);
        assertThat(postCommentAdminDto.getCurrentPage()).isEqualTo(1);
        assertThat(postCommentAdminDto.getPageNumbers()).isEqualTo(pageNumbers);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(commentService).getCommentsByPost(any(Post.class), pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("addingDateTime")).isNotNull();

        verify(adminCommentPageTracker).getCurrentPage();
        verify(adminCommentPageTracker).setCurrentPage(1);
        verify(adminCommentPageTracker).setPostId(ID);
    }

    @Test
    public void getCommentsByPostWithLastPageProblem() {
        List<Comment> comments = Arrays.asList(
                Comment.builder().build(), Comment.builder().build()
        );
        Page<Comment> commentPage = Mockito.mock(Page.class);
        Set<Integer> pageNumbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        when(postService.getPostById(anyLong())).thenReturn(Post.builder().id(ID).title(TITLE)
                .user(User.builder().id(ID).build()).build());
        when(adminCommentPageTracker.getPostId()).thenReturn(ID);
        when(adminCommentPageTracker.getCurrentPage()).thenReturn(1);
        when(commentPage.stream()).thenReturn(comments.stream());
        when(commentService.getCommentsByPost(any(Post.class), any(Pageable.class)))
                .thenReturn(commentPage);
        when(adminCommentPageTracker.getPageNumbersForPagination(commentPage))
                .thenReturn(pageNumbers);

        PostCommentAdminDto postCommentAdminDto =
                adminService.getCommentsForPost(ID, 2, 5, true);

        assertThat(postCommentAdminDto.getId()).isEqualTo(ID);
        assertThat(postCommentAdminDto.getTitle()).isEqualTo(TITLE);
        assertThat(postCommentAdminDto.getCommentAdminDtos().size()).isEqualTo(2);
        assertThat(postCommentAdminDto.getCurrentPage()).isEqualTo(1);
        assertThat(postCommentAdminDto.getPageNumbers()).isEqualTo(pageNumbers);

        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(commentService, times(2)).getCommentsByPost(any(Post.class),
                pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("addingDateTime")).isNotNull();

        verify(adminCommentPageTracker).getCurrentPage();
        verify(adminCommentPageTracker).setCurrentPage(1);
        verify(adminCommentPageTracker).setPostId(ID);
    }

    @Test
    public void deleteCommentById() {
        adminService.deleteCommentById(ID);
        verify(commentService).deleteCommentById(ID);
    }

    @Test
    public void deletePostWithCommentsById() {
        Post post = Post.builder().id(ID).build();
        when(postService.getPostById(ID)).thenReturn(post);

        adminService.deletePostWithCommentsById(ID);

        verify(commentService).deleteCommentsByPost(post);
        verify(postService).deletePost(post);
    }

    @Test
    public void deleteUserWithPostById() {
        AdminServiceImpl spyService = Mockito.spy(adminService);
        User user = User.builder().id(ID).build();
        List<Post> posts = Arrays.asList(
            Post.builder().id(ID).build(),
            Post.builder().id(2L).build()
        );
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(postService.getPostsByUser(any(User.class))).thenReturn(posts);

        spyService.deleteUserWithPostsById(ID);

        verify(userService).getUserById(ID);
        verify(postService).getPostsByUser(user);
        verify(spyService, times(2)).deletePostWithCommentsById(anyLong());
        verify(userService).deleteUser(user);
    }

    @Test
    public void changeAdminPasswordAndLogout() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        User user = User.builder().id(ID).password("password").build();
        when(userService.getUserByEmail(AdminService.ADMIN_EMAIL)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("password");

        adminService.changeAdminPasswordAndLogout("newPassword", request);

        verify(userService).saveOrUpdateUser(user);
        verify(request).logout();
    }

    @Test
    public void resetAdminPassword() {
        AdminServiceImpl spyService = Mockito.spy(adminService);
        User user = User.builder().id(ID).password("password").build();
        when(userService.getUserByEmail(AdminService.ADMIN_EMAIL)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("password");

        spyService.resetAdminPassword();

        verify(userService).saveOrUpdateUser(user);
    }
}