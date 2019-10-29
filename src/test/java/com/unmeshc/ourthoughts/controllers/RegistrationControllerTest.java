package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.exceptions.EmailNotSentException;
import com.unmeshc.ourthoughts.services.RegistrationService;
import com.unmeshc.ourthoughts.services.VerificationTokenService;
import com.unmeshc.ourthoughts.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegistrationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private VerificationTokenService tokenService;

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
               .andExpect(view().name(RegistrationController.REGISTRATION_FORM));

        verifyZeroInteractions(userService);
        verifyZeroInteractions(registrationService);
        verifyZeroInteractions(tokenService);
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
                .andExpect(view().name(RegistrationController.REGISTRATION_FORM));

        verifyZeroInteractions(userService);
        verifyZeroInteractions(registrationService);
        verifyZeroInteractions(tokenService);
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
                .andExpect(view().name(RegistrationController.REGISTRATION_FORM));

        verifyZeroInteractions(userService);
        verifyZeroInteractions(registrationService);
        verifyZeroInteractions(tokenService);
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
                .andExpect(view().name(RegistrationController.REGISTRATION_FORM));

        verifyZeroInteractions(userService);
        verifyZeroInteractions(registrationService);
        verifyZeroInteractions(tokenService);
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
                .andExpect(view().name(RegistrationController.REGISTRATION_FORM));

        verify(userService).isEmailExists("unmeshchow@gmail.com");
        verifyZeroInteractions(registrationService);
        verifyZeroInteractions(tokenService);
    }

    @Test(expected = NestedServletException.class)
    public void saveRegistrationDataThrowException() throws Exception {
        when(userService.isEmailExists(anyString())).thenReturn(false);
        when(registrationService.saveUserAndVerifyEmail(any(), any()))
                .thenThrow(EmailNotSentException.class);

        mockMvc.perform(post("/registration/save")
                .param("firstName", "Unmesh")
                .param("lastName", "Chowdhury")
                .param("email", "unmeshchow@gmail.com")
                .param("password", "password")
                .param("matchingPassword", "password"));
    }

    @Test
    public void saveRegistrationData() throws Exception {
        when(userService.isEmailExists(anyString())).thenReturn(false);

        mockMvc.perform(post("/registration/save")
                    .param("firstName", "Unmesh")
                    .param("lastName", "Chowdhury")
                    .param("email", "unmeshchow@gmail.com")
                    .param("password", "password")
                    .param("matchingPassword", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RegistrationController.REDIRECT_REGISTRATION_SUCCESS));

        verify(userService).isEmailExists("unmeshchow@gmail.com");
        verify(registrationService).saveUserAndVerifyEmail(any(UserCommand.class),
                any(HttpServletRequest.class)); // TODO
        verifyZeroInteractions(tokenService);
    }

    @Test
    public void successRegistration() throws Exception {
        mockMvc.perform(get("/registration/success"))
               .andExpect(status().isOk())
               .andExpect(view().name(RegistrationController.REGISTRATION_SUCCESS));

        verifyZeroInteractions(userService);
        verifyZeroInteractions(registrationService);
        verifyZeroInteractions(tokenService);
    }

    @Test
    public void activateRegistrationInvalidToken() throws Exception {
        when(tokenService.getByToken(anyString())).thenReturn(null);

        mockMvc.perform(get("/registration/confirm")
                   .param("token", "token"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(RegistrationController.REDIRECT_REGISTRATION_CONFIRM_BAD));

        verify(tokenService).getByToken(anyString());
        verifyZeroInteractions(userService);
        verifyZeroInteractions(registrationService);
    }

    @Test
    public void activateRegistrationExpiredToken() throws Exception {
        VerificationToken token = VerificationToken.builder().id(1L).expiryDate(LocalDateTime.now().minusDays(1L)).build();
        when(tokenService.getByToken(anyString())).thenReturn(token);

        mockMvc.perform(get("/registration/confirm")
                .param("token", "token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RegistrationController.REDIRECT_REGISTRATION_CONFIRM_BAD));

        verify(tokenService).getByToken(anyString());
        verifyZeroInteractions(userService);
        verifyZeroInteractions(registrationService);
    }

    @Test
    public void activateRegistration() throws Exception {
        VerificationToken token = VerificationToken.builder().id(1L).expiryDate(LocalDateTime.now().plusDays(1L))
                .user(User.builder().build()).build();
        when(tokenService.getByToken(anyString())).thenReturn(token);

        mockMvc.perform(get("/registration/confirm")
                .param("token", "token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RegistrationController.REDIRECT_LOGIN));

        verify(tokenService).getByToken(anyString());
        verifyZeroInteractions(userService);
        verify(registrationService).activateUser(any(User.class)); // TODO
    }

    @Test
    public void badToken() throws Exception {
        mockMvc.perform(get("/registration/confirm/bad"))
               .andExpect(status().isOk())
               .andExpect(view().name(RegistrationController.CONFIRM_BAD));
    }
}