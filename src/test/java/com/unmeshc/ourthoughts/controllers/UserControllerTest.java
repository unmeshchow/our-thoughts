package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.configurations.SecurityUtils;
import com.unmeshc.ourthoughts.controllers.pagination.SearchPostPageTracker;
import com.unmeshc.ourthoughts.converters.UserToUserProfileDto;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.UserProfileDto;
import com.unmeshc.ourthoughts.services.CommentService;
import com.unmeshc.ourthoughts.services.PostService;
import com.unmeshc.ourthoughts.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private PostService postService;

    @Mock
    private ControllerUtils controllerUtils;

    @Mock
    private SearchPostPageTracker searchPostPageTracker;

    @Mock
    private UserToUserProfileDto userToUserProfileDto;

    @InjectMocks
    private UserController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                  .setControllerAdvice(new ControllerExceptionHandler()).build();
    }

    @Test
    public void showCreatePostForm() throws Exception {
        mockMvc.perform(get("/user/create/post/form"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("postCommand"))
               .andExpect(view().name(UserController.CREATE_POST_FORM));
    }

    @Test
    public void processCreatePostAllFieldsAreEmpty() throws Exception {
        ClassPathResource image = new ClassPathResource("/static/img/avatar.jpg");
        InputStream inputStream = image.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("myImage",
                "avatar.jpg", "image/jpeg", inputStream);

        mockMvc.perform(multipart("/user/create/post").file(multipartFile)
                    .param("title", "")
                    .param("body", "")
                    .param("caption", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("postCommand"))
                .andExpect(model().attributeHasFieldErrors("postCommand", "title"))
                .andExpect(model().attributeHasFieldErrors("postCommand", "body"))
                .andExpect(model().attributeHasFieldErrors("postCommand", "caption"))
                .andExpect(view().name(UserController.CREATE_POST_FORM));
    }

    @Test
    public void processCreatePostIncorrectPostPhoto() throws Exception {
        ClassPathResource image = new ClassPathResource("/static/img/avatar.jpg");
        InputStream inputStream = image.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("myImage",
                "avatar.jpg", "image/jpeg", inputStream);
        when(controllerUtils.isNotCorrectPostPhoto(any())).thenReturn(true);

        mockMvc.perform(multipart("/user/create/post").file(multipartFile)
                .param("title", "title")
                .param("body", "body")
                .param("caption", "caption"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("postCommand"))
                .andExpect(model().attributeHasFieldErrors("postCommand", "photo"))
                .andExpect(view().name(UserController.CREATE_POST_FORM));
    }

    @Test
    public void processCreatePost() throws Exception {
        ClassPathResource image = new ClassPathResource("/static/img/avatar.jpg");
        InputStream inputStream = image.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("photo",
                "avatar.jpg", "image/jpeg", inputStream);
        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);
        when(controllerUtils.convertIntoByteArray(multipartFile)).thenReturn(new Byte[]{});

        mockMvc.perform(multipart("/user/create/post").file(multipartFile)
                    .param("title", "title")
                    .param("body", "body")
                    .param("caption", "caption"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(UserController.REDIRECT_INDEX));

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(controllerUtils).convertIntoByteArray(multipartFile);
        verify(searchPostPageTracker).newPost();
        ArgumentCaptor<PostCommand> postCommandArgumentCaptor =
                ArgumentCaptor.forClass(PostCommand.class);
        verify(postService).savePostForUser(eq(user), postCommandArgumentCaptor.capture());
        PostCommand postCommand = postCommandArgumentCaptor.getValue();
        assertThat(postCommand.getPostPhoto()).isNotNull();
        assertThat(postCommand.getTitle()).isEqualTo("title");
        assertThat(postCommand.getBody()).isEqualTo("body");
        assertThat(postCommand.getCaption()).isEqualTo("caption");
    }

    @Test
    public void showProfile() throws Exception {
        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        when(userToUserProfileDto.convert(user)).thenReturn(UserProfileDto.builder().build());
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userProfileDto"))
                .andExpect(view().name(UserController.MY_PROFILE));

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(userToUserProfileDto).convert(user);
    }

    @Test
    public void showChangeImageForm() throws Exception {
        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/user/change/image/form"))
                .andExpect(status().isOk())
                .andExpect(view().name(UserController.UPLOAD_IMAGE));

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
    }

    @Test
    public void changeImage() throws Exception {
        ClassPathResource image = new ClassPathResource("/static/img/avatar.jpg");
        InputStream inputStream = image.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("myImage",
                "avatar.jpg", "image/jpeg", inputStream);
        Byte[] bytes = new Byte[10];

        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);
        when(controllerUtils.convertIntoByteArray(multipartFile)).thenReturn(bytes);

        mockMvc.perform(multipart("/user/change/image").file(multipartFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("error", false))
                .andExpect(view().name(UserController.REDIRECT_USER_PROFILE));

        user.setImage(bytes);

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(controllerUtils).convertIntoByteArray(multipartFile);
        verify(userService).saveOrUpdate(user);
    }

    @Test
    public void changeImageIncorrectUserImage() throws Exception {
        ClassPathResource image = new ClassPathResource("/static/img/avatar.jpg");
        InputStream inputStream = image.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("myImage",
                "avatar.jpg", "image/jpeg", inputStream);

        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);
        when(controllerUtils.isNotCorrectUserImage(any(MultipartFile.class))).thenReturn(true);

        mockMvc.perform(multipart("/user/change/image").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", true))
                .andExpect(view().name(UserController.UPLOAD_IMAGE));

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(controllerUtils).isNotCorrectUserImage(any(MultipartFile.class));
    }

    @Test
    public void obtainImage() throws Exception {
        Byte[] bytes = new Byte[10];
        byte[] imageBytes = new byte[10];
        User user = User.builder().id(1L).email("unmesh@gmail.com").image(bytes).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);
        when(controllerUtils.convertIntoByteArray(bytes)).thenReturn(imageBytes);

        mockMvc.perform(get("/user/get/image"))
                .andExpect(status().isOk());
        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();

        verify(controllerUtils).copyBytesToResponse(
                any(HttpServletResponse.class), eq(imageBytes));
    }

    @Test
    public void addCommentPostNotFoundException() throws Exception {
        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);
        when(postService.getById(anyLong())).thenReturn(null);

        mockMvc.perform(post("/user/comment/post/" + 1 + "/add"))
               .andExpect(status().isNotFound());

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
    }

    @Test
    public void addCommentPostEmptyComment() throws Exception {
        Post post = Post.builder().id(1L).build();
        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);
        when(postService.getById(anyLong())).thenReturn(post);

        mockMvc.perform(post("/user/comment/post/" + 1 + "/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/visitor/post/" + 1 + "/details"));

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(commentService).saveCommentOfUserForPost("", user, post);
    }

    @Test
    public void addCommentPost() throws Exception {
        Post post = Post.builder().id(1L).build();
        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);
        when(postService.getById(anyLong())).thenReturn(post);

        mockMvc.perform(post("/user/comment/post/" + 1 + "/add")
                    .param("comment", "Excellent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/visitor/post/" + 1 + "/details"));

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(commentService).saveCommentOfUserForPost("Excellent", user, post);
    }
}