package com.unmeshc.ourthoughts.controllers;

public class RegistrationControllerTest {
/*
    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void showRegistrationForm() throws Exception {
        mockMvc.perform(get("/registration/form"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("userCommand"))
               .andExpect(view().name("register/registrationForm"));

        verifyZeroInteractions(userService);
    }

    @Test
    public void saveRegistrationDataAllFieldsAreEmpty() throws Exception {
        mockMvc.perform(post("/registration/save")
                    .param("firstName", "")
                    .param("lastName", "")
                    .param("email", "")
                    .param("password", "")
                    .param("matchingPassword", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("userCommand"))
                .andExpect(model().attributeHasFieldErrors("userCommand",
                        "firstName"))
                .andExpect(model().attributeHasFieldErrors("userCommand",
                        "lastName"))
                .andExpect(model().attributeHasFieldErrors("userCommand",
                        "email"))
                .andExpect(model().attributeHasFieldErrors("userCommand",
                        "password"))
                .andExpect(model().attributeHasFieldErrors("userCommand",
                        "matchingPassword"))
                .andExpect(view().name("register/registrationForm"));

        verifyZeroInteractions(userService);
    }

    @Test
    public void saveRegistrationDataInvalidEmail() throws Exception {
        mockMvc.perform(post("/registration/save")
                    .param("firstName", "Unmesh")
                    .param("lastName", "Chowdhury")
                    .param("email", "unmeshchow@gmailcom")
                    .param("password", "password")
                    .param("matchingPassword", "password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("userCommand"))
                .andExpect(model().attributeHasFieldErrors("userCommand",
                        "email"))
                .andExpect(view().name("register/registrationForm"));

        verifyZeroInteractions(userService);
    }

    @Test
    public void saveRegistrationDataPasswordsAreNotSame() throws Exception {
        mockMvc.perform(post("/registration/save")
                    .param("firstName", "Unmesh")
                    .param("lastName", "Chowdhury")
                    .param("email", "unmeshchow@gmail.com")
                    .param("password", "password")
                    .param("matchingPassword", "passw"))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(1))
                .andExpect(view().name("register/registrationForm"));

        verifyZeroInteractions(userService);
    }

    @Test
    public void saveRegistrationDataEmailExists() throws Exception {
        when(userService.isEmailExists(anyString())).thenReturn(true);

        mockMvc.perform(post("/registration/save")
                    .param("firstName", "Unmesh")
                    .param("lastName", "Chowdhury")
                    .param("email", "unmeshchow@gmail.com")
                    .param("password", "password")
                    .param("matchingPassword", "password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("userCommand"))
                .andExpect(model().attributeHasFieldErrors("userCommand",
                        "email"))
                .andExpect(view().name("register/registrationForm"));

        verify(userService).isEmailExists("unmeshchow@gmail.com");
    }

    @Test
    public void saveRegistrationData() throws Exception {
        when(userService.isEmailExists(anyString())).thenReturn(false);
        when(userService.saveUser(any(UserCommand.class))).thenReturn(
                User.builder().id(1L).build());

        mockMvc.perform(post("/registration/save")
                    .param("firstName", "Unmesh")
                    .param("lastName", "Chowdhury")
                    .param("email", "unmeshchow@gmail.com")
                    .param("password", "password")
                    .param("matchingPassword", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("success"));

        verify(userService).isEmailExists("unmeshchow@gmail.com");
        verify(userService).saveUser(any(UserCommand.class));
    }*/



    /*









    @Test
    public void processUserRegistrationEmailFail() throws Exception {
        User user = User.builder().id(1L).build();
        when(userService.isEmailExists(anyString())).thenReturn(false);
        when(userService.registerUser(any(UserCommand.class))).thenReturn(user);
        doThrow(RuntimeException.class).when(applicationEventPublisher)
                .publishEvent(any(ApplicationEvent.class));

        mockMvc.perform(post("/user/registration")
                .param("firstName", "Unmesh")
                .param("lastName", "Chowdhury")
                .param("email", "unmeshchow@gmail.com")
                .param("password", "password")
                .param("matchingPassword", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/emailError"));

        verify(userService).isEmailExists("unmeshchow@gmail.com");
        verify(userService).registerUser(any(UserCommand.class));
        verify(applicationEventPublisher).publishEvent(any());
    }

    @Test
    public void processUserRegistration() throws Exception {
        User user = User.builder().id(1L).build();
        when(userService.isEmailExists(anyString())).thenReturn(false);
        when(userService.registerUser(any(UserCommand.class))).thenReturn(user);
        doNothing().when(applicationEventPublisher).publishEvent(any(ApplicationEvent.class));

        mockMvc.perform(post("/user/registration")
                .param("firstName", "Unmesh")
                .param("lastName", "Chowdhury")
                .param("email", "unmeshchow@gmail.com")
                .param("password", "password")
                .param("matchingPassword", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/successRegister"));

        verify(userService).isEmailExists("unmeshchow@gmail.com");
        verify(userService).registerUser(any(UserCommand.class));
        verify(applicationEventPublisher).publishEvent(any());
    }*/
}