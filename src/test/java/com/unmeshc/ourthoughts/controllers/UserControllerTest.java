package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PostCommand;
import com.unmeshc.ourthoughts.configurations.security.SecurityUtils;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.UserProfileDto;
import com.unmeshc.ourthoughts.services.UserService;
import com.unmeshc.ourthoughts.services.exceptions.NotFoundException;
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

import static com.unmeshc.ourthoughts.TestLiterals.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private ControllerUtils controllerUtils;

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
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile",
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
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile",
                "avatar.jpg", "image/jpeg", inputStream);
        when(controllerUtils.isNotCorrectPostPhoto(any())).thenReturn(true);

        mockMvc.perform(multipart("/user/create/post").file(multipartFile)
                .param("title", TITLE)
                .param("body", BODY)
                .param("caption", CAPTION))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("postCommand"))
                .andExpect(model().attributeHasFieldErrors("postCommand", "multipartFile"))
                .andExpect(view().name(UserController.CREATE_POST_FORM));
    }

    @Test
    public void processCreatePost() throws Exception {
        ClassPathResource image = new ClassPathResource("/static/img/avatar.jpg");
        InputStream inputStream = image.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("multipartFile",
                "avatar.jpg", "image/jpeg", inputStream);
        User user = User.builder().id(ID).email(EMAIL).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn(EMAIL);
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(multipart("/user/create/post").file(multipartFile)
                    .param("title", TITLE)
                    .param("body", BODY)
                    .param("caption", CAPTION))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(UserController.REDIRECT_INDEX));

        ArgumentCaptor<PostCommand> postCommandArgumentCaptor =
                ArgumentCaptor.forClass(PostCommand.class);

        verify(userService).savePostForUser(eq(user), postCommandArgumentCaptor.capture());

        PostCommand postCommand = postCommandArgumentCaptor.getValue();
        assertThat(postCommand.getMultipartFile()).isNotNull();
        assertThat(postCommand.getTitle()).isEqualTo(TITLE);
        assertThat(postCommand.getBody()).isEqualTo(BODY);
        assertThat(postCommand.getCaption()).isEqualTo(CAPTION);
    }

    @Test
    public void showProfile() throws Exception {
        User user = User.builder().id(ID).email(EMAIL).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn(EMAIL);
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(userService.getUserProfile(any(User.class)))
                .thenReturn(UserProfileDto.builder().build());

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userProfileDto"))
                .andExpect(view().name(UserController.MY_PROFILE));

        verify(userService).getUserProfile(user);
    }

    @Test
    public void showChangeImageForm() throws Exception {
        User user = User.builder().id(ID).email(EMAIL).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn(EMAIL);
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/user/change/image/form"))
                .andExpect(status().isOk())
                .andExpect(view().name(UserController.UPLOAD_IMAGE));

        verify(userService).getUserByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
    }

    @Test
    public void changeImage() throws Exception {
        ClassPathResource image = new ClassPathResource("/static/img/avatar.jpg");
        InputStream inputStream = image.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("myImage",
                "avatar.jpg", "image/jpeg", inputStream);
        User user = User.builder().id(ID).email(EMAIL).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn(EMAIL);
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(multipart("/user/change/image").file(multipartFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("error", false))
                .andExpect(view().name(UserController.REDIRECT_USER_PROFILE));

        ArgumentCaptor<MultipartFile> multipartFileArgumentCaptor =
                ArgumentCaptor.forClass(MultipartFile.class);
        verify(userService).changeImageForUser(eq(user), multipartFileArgumentCaptor.capture());

        MultipartFile imageFile = multipartFileArgumentCaptor.getValue();
        assertThat(imageFile.getContentType()).isEqualTo("image/jpeg");
        assertThat(imageFile.getName()).isEqualTo("myImage");
        assertThat(imageFile.getOriginalFilename()).isEqualTo("avatar.jpg");
    }

    @Test
    public void changeImageIncorrectUserImage() throws Exception {
        ClassPathResource image = new ClassPathResource("/static/img/avatar.jpg");
        InputStream inputStream = image.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("myImage",
                "avatar.jpg", "image/jpeg", inputStream);

        User user = User.builder().id(ID).email(EMAIL).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn(EMAIL);
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(controllerUtils.isNotCorrectUserImage(any(MultipartFile.class))).thenReturn(true);

        mockMvc.perform(multipart("/user/change/image").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", true))
                .andExpect(view().name(UserController.UPLOAD_IMAGE));

        verify(userService).getUserByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(controllerUtils).isNotCorrectUserImage(any(MultipartFile.class));
    }

    @Test
    public void obtainImage() throws Exception {
        byte[] bytes = new byte[100];
        User user = User.builder().id(ID).email(EMAIL).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn(EMAIL);
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(userService.getImageForUser(any(User.class))).thenReturn(bytes);


        mockMvc.perform(get("/user/get/image"))
                .andExpect(status().isOk());

        verify(controllerUtils).copyBytesToResponse(any(HttpServletResponse.class), eq(bytes));
        verify(userService).getImageForUser(user);
    }

    @Test
    public void addCommentPostNotFound() throws Exception {
        User user = User.builder().id(ID).email(EMAIL).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn(EMAIL);
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        doThrow(NotFoundException.class).when(userService).saveCommentOfUserForPost(
                anyString(), any(User.class), anyLong());

        mockMvc.perform(post("/user/comment/post/" + 1 + "/add"))
               .andExpect(status().isNotFound());

        verify(userService).getUserByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
    }

    @Test
    public void addCommentPostEmptyComment() throws Exception {
        User user = User.builder().id(ID).email(EMAIL).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn(EMAIL);
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(post("/user/comment/post/" + 1 + "/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/visitor/post/" + 1
                        + "/details"));

        verify(userService).saveCommentOfUserForPost("", user, ID);
    }

    @Test
    public void addCommentPost() throws Exception {
        User user = User.builder().id(ID).email(EMAIL).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn(EMAIL);
        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(post("/user/comment/post/" + 1 + "/add")
                    .param("comment", "Excellent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/visitor/post/"
                        + 1 + "/details"));

        verify(userService).saveCommentOfUserForPost("Excellent", user, ID);
    }
}