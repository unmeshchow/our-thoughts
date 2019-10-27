package com.unmeshc.ourthoughts.controllers;

public class UserControllerTest {
/*
    @Mock
    private UserService userService;

    @Mock
    private UserToUserCommand userToUserCommand;

    @Mock
    private ImageService imageService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private UserController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void showCreatePostForm() throws Exception {
        mockMvc.perform(get("/user/create/post/form"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("postCommand"))
               .andExpect(view().name(UserController.CREATE_POST_FORM));
    }

    @Test
    public void showProfile() throws Exception {
        User user = User.builder().id(1L).email("unmesh@gmail.com").build();
        when(userToUserCommand.convert(any())).thenReturn(UserCommand.builder().build());
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userCommand"))
                .andExpect(view().name(UserController.MY_PROFILE));

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(userToUserCommand).convert(any());
        verifyZeroInteractions(imageService);
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
        verifyZeroInteractions(userToUserCommand);
        verifyZeroInteractions(imageService);
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
        when(imageService.convertIntoByteArray(multipartFile)).thenReturn(bytes);

        mockMvc.perform(multipart("/user/change/image").file(multipartFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(UserController.REDIRECT_USER_PROFILE));

        user.setImage(bytes);

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(imageService).convertIntoByteArray(multipartFile);
        verify(userService).saveOrUpdateUser(user);
        verifyZeroInteractions(userToUserCommand);
    }

    @Test
    public void obtainImage() throws Exception {
        Byte[] bytes = new Byte[10];
        byte[] imageBytes = new byte[10];
        User user = User.builder().id(1L).email("unmesh@gmail.com").image(bytes).build();
        when(securityUtils.getEmailFromSecurityContext()).thenReturn("unmesh@gmail.com");
        when(userService.getByEmail(anyString())).thenReturn(user);
        when(imageService.convertIntoByteArray(bytes)).thenReturn(imageBytes);

        mockMvc.perform(get("/user/get/image"))
                .andExpect(status().isOk());

        verify(userService).getByEmail(anyString());
        verify(securityUtils).getEmailFromSecurityContext();
        verify(imageService).convertIntoByteArray(bytes);
        verifyZeroInteractions(userToUserCommand);
    }*/
}