package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.UserCommand;
import com.unmeshc.ourthoughts.services.RegistrationService;
import com.unmeshc.ourthoughts.services.exceptions.BadVerificationTokenException;
import com.unmeshc.ourthoughts.services.exceptions.EmailNotSentException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

import static com.unmeshc.ourthoughts.TestLiterals.*;
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
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                  .setControllerAdvice(new ControllerExceptionHandler()).build();
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
                    .param("firstName", FIRST_NAME)
                    .param("lastName", LAST_NAME)
                    .param("email", "unmesh@gmailcom")
                    .param("password", PASSWORD)
                    .param("matchingPassword", PASSWORD))
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
                    .param("firstName", FIRST_NAME)
                    .param("lastName", LAST_NAME)
                    .param("email", EMAIL)
                    .param("password", PASSWORD)
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
                    .param("firstName", FIRST_NAME)
                    .param("lastName", LAST_NAME)
                    .param("email", EMAIL)
                    .param("password", PASSWORD)
                    .param("matchingPassword", PASSWORD))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("userCommand"))
                .andExpect(model().attributeHasFieldErrors("userCommand",
                        "email"))
                .andExpect(view().name(RegistrationController.REGISTRATION_FORM));

        verify(registrationService).isUserEmailExists(EMAIL);
    }

    @Test
    public void saveRegistrationDataEmailNotSent() throws Exception {
        when(registrationService.isUserEmailExists(anyString())).thenReturn(false);
        doThrow(EmailNotSentException.class).when(registrationService)
                .saveUserAndVerifyByEmailing(any(), any());

        mockMvc.perform(post("/registration/save")
                   .param("firstName", FIRST_NAME)
                    .param("lastName", LAST_NAME)
                    .param("email", EMAIL)
                    .param("password", PASSWORD)
                    .param("matchingPassword", PASSWORD))
               .andExpect(status().is5xxServerError()) ;
    }

    @Test
    public void saveRegistrationData() throws Exception {
        when(registrationService.isUserEmailExists(anyString())).thenReturn(false);

        mockMvc.perform(post("/registration/save")
                    .param("firstName", FIRST_NAME)
                    .param("lastName", LAST_NAME)
                    .param("email", EMAIL)
                    .param("password", PASSWORD)
                    .param("matchingPassword", PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RegistrationController.REDIRECT_REGISTRATION_SUCCESS));

        verify(registrationService).isUserEmailExists(EMAIL);
        ArgumentCaptor<UserCommand> userCommandArgumentCaptor = ArgumentCaptor.forClass(
                UserCommand.class);
        verify(registrationService).saveUserAndVerifyByEmailing(
                userCommandArgumentCaptor.capture(), any(HttpServletRequest.class));

        UserCommand userCommand = userCommandArgumentCaptor.getValue();
        assertThat(userCommand.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(userCommand.getLastName()).isEqualTo(LAST_NAME);
        assertThat(userCommand.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    public void successRegistration() throws Exception {
        mockMvc.perform(get("/registration/success"))
               .andExpect(status().isOk())
               .andExpect(view().name(RegistrationController.REGISTRATION_SUCCESS));

        verifyZeroInteractions(registrationService);
    }

    @Test
    public void activateRegistrationBadVerificationToken() throws Exception {
        doThrow(BadVerificationTokenException.class).when(registrationService)
                .activateUserByVerificationToken(anyString());

        mockMvc.perform(get("/registration/confirm")
                   .param("token", TOKEN))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(RegistrationController.REDIRECT_REGISTRATION_CONFIRM_BAD));
    }

    @Test
    public void activateRegistration() throws Exception {
        mockMvc.perform(get("/registration/confirm")
                .param("token", TOKEN))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RegistrationController.REDIRECT_LOGIN));

        verify(registrationService).activateUserByVerificationToken(TOKEN);
    }

    @Test
    public void badToken() throws Exception {
        mockMvc.perform(get("/registration/confirm/bad"))
               .andExpect(status().isOk())
               .andExpect(view().name(RegistrationController.CONFIRM_BAD));
    }
}