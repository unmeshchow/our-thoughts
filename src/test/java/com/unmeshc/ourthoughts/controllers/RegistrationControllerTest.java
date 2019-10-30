package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.exceptions.EmailNotSentException;
import com.unmeshc.ourthoughts.services.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegistrationControllerTest {

    @Mock
    private RegistrationService registrationService;

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

        verifyZeroInteractions(registrationService);
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

        verifyZeroInteractions(registrationService);
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

        verifyZeroInteractions(registrationService);
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

        verifyZeroInteractions(registrationService);
    }

    @Test
    public void saveRegistrationDataEmailExists() throws Exception {
        when(registrationService.isUserEmailExists(anyString())).thenReturn(true);

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

        verify(registrationService).isUserEmailExists("unmeshchow@gmail.com");
    }

    @Test(expected = NestedServletException.class)
    public void saveRegistrationDataThrowException() throws Exception {
        when(registrationService.isUserEmailExists(anyString())).thenReturn(false);
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
        when(registrationService.isUserEmailExists(anyString())).thenReturn(false);

        mockMvc.perform(post("/registration/save")
                    .param("firstName", "Unmesh")
                    .param("lastName", "Chowdhury")
                    .param("email", "unmeshchow@gmail.com")
                    .param("password", "password")
                    .param("matchingPassword", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RegistrationController.REDIRECT_REGISTRATION_SUCCESS));

        verify(registrationService).isUserEmailExists("unmeshchow@gmail.com");
        ArgumentCaptor<UserCommand> userCommandArgumentCaptor =
                ArgumentCaptor.forClass(UserCommand.class);
        verify(registrationService).saveUserAndVerifyEmail(userCommandArgumentCaptor.capture(),
                any(HttpServletRequest.class));
        UserCommand userCommand = userCommandArgumentCaptor.getValue();
        assertThat(userCommand.getFirstName()).isEqualTo("Unmesh");
        assertThat(userCommand.getLastName()).isEqualTo("Chowdhury");
        assertThat(userCommand.getEmail()).isEqualTo("unmeshchow@gmail.com");
    }

    @Test
    public void successRegistration() throws Exception {
        mockMvc.perform(get("/registration/success"))
               .andExpect(status().isOk())
               .andExpect(view().name(RegistrationController.REGISTRATION_SUCCESS));

        verifyZeroInteractions(registrationService);
    }

    @Test
    public void activateRegistrationInvalidToken() throws Exception {
        when(registrationService.getVerificationTokenByToken(anyString())).thenReturn(null);

        mockMvc.perform(get("/registration/confirm")
                   .param("token", "token"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(RegistrationController.REDIRECT_REGISTRATION_CONFIRM_BAD));

        verify(registrationService).getVerificationTokenByToken("token");
    }

    @Test
    public void activateRegistrationExpiredToken() throws Exception {
        VerificationToken token = VerificationToken.builder().id(1L).expiryDate(LocalDateTime.now().minusDays(1L)).build();
        when(registrationService.getVerificationTokenByToken(anyString())).thenReturn(token);

        mockMvc.perform(get("/registration/confirm")
                .param("token", "token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RegistrationController.REDIRECT_REGISTRATION_CONFIRM_BAD));

        verify(registrationService).getVerificationTokenByToken(anyString());
    }

    @Test
    public void activateRegistration() throws Exception {
        VerificationToken token = VerificationToken.builder().id(1L).expiryDate(LocalDateTime.now().plusDays(1L))
                .user(User.builder().build()).build();
        when(registrationService.getVerificationTokenByToken(anyString())).thenReturn(token);

        mockMvc.perform(get("/registration/confirm")
                .param("token", "token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RegistrationController.REDIRECT_LOGIN));

        verify(registrationService).getVerificationTokenByToken(anyString());
        verify(registrationService).activateUser(any(User.class));
    }

    @Test
    public void badToken() throws Exception {
        mockMvc.perform(get("/registration/confirm/bad"))
               .andExpect(status().isOk())
               .andExpect(view().name(RegistrationController.CONFIRM_BAD));
    }
}