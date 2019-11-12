package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.services.PasswordService;
import com.unmeshc.ourthoughts.services.exceptions.BadVerificationTokenException;
import com.unmeshc.ourthoughts.services.exceptions.NotFoundException;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PasswordControllerTest {

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private PasswordController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                  .setControllerAdvice(new ControllerExceptionHandler()).build();
    }

    @Test
    public void showPasswordResetForm() throws Exception {
        mockMvc.perform(get("/password/reset/form"))
               .andExpect(status().isOk())
               .andExpect(view().name(PasswordController.PASSWORD_RESET_FORM));
    }

    @Test
    public void processPasswordReset() throws Exception {
        mockMvc.perform(get("/password/reset/send")
                   .param("email", EMAIL))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(PasswordController.REDIRECT_PASSWORD_RESET_SUCCESS));

        verify(passwordService).verifyResetPasswordForUserByEmailing(eq(EMAIL),
                any(HttpServletRequest.class));
    }

    @Test
    public void processPasswordResetNotFound() throws Exception {
        doThrow(NotFoundException.class).when(passwordService)
            .verifyResetPasswordForUserByEmailing(anyString(), any(HttpServletRequest.class));

        mockMvc.perform(get("/password/reset/send")
                    .param("email", "unmesh@gmail.com"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void passwordResetSuccess() throws Exception {
        mockMvc.perform(get("/password/reset/success"))
               .andExpect(status().isOk())
               .andExpect(view().name(PasswordController.PASSWORD_RESET_SUCCESS));
    }

    @Test
    public void acceptPasswordResetBadVerificationTokenException() throws Exception {
        doThrow(BadVerificationTokenException.class).when(passwordService)
                .checkAndSetChangePasswordPrivilege(anyString());

        mockMvc.perform(get("/password/reset/confirm")
                   .param("token", TOKEN))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(PasswordController.REDIRECT_PASSWORD_RESET_CONFIRM_BAD));
    }

    @Test
    public void acceptPasswordReset() throws Exception {
        mockMvc.perform(get("/password/reset/confirm")
                .param("token", TOKEN))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(PasswordController.REDIRECT_PASSWORD_RESET_UPDATE_FORM));

        verify(passwordService).checkAndSetChangePasswordPrivilege(TOKEN);
    }

    @Test
    public void badToken() throws Exception {
        mockMvc.perform(get("/password/reset/confirm/bad"))
                .andExpect(status().isOk())
                .andExpect(view().name(PasswordController.BAD_TOKEN));
    }

    @Test
    public void showPasswordUpdateForm() throws Exception {
        mockMvc.perform(get("/password/reset/update/form"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("passwordCommand"))
               .andExpect(view().name(PasswordController.PASSWORD_UPDATE_FORM));
    }

    @Test
    public void resetPasswordAllFieldsAreEmpty() throws Exception {
        mockMvc.perform(post("/password/reset/update")
                   .param("password", "")
                   .param("matchingPassword", ""))
               .andExpect(status().isOk())
               .andExpect(model().attributeHasErrors("passwordCommand"))
               .andExpect(model().attributeHasFieldErrors("passwordCommand", "password"))
               .andExpect(model().attributeHasFieldErrors("passwordCommand", "matchingPassword"))
               .andExpect(view().name(PasswordController.PASSWORD_UPDATE_FORM));

        verifyZeroInteractions(passwordService);
    }

    @Test
    public void resetPasswordPasswordsAreNotSame() throws Exception {
        mockMvc.perform(post("/password/reset/update")
                   .param("password", PASSWORD)
                   .param("matchingPassword", PASSWORD + "x"))
               .andExpect(status().isOk())
               .andExpect(model().errorCount(1))
               .andExpect(view().name(PasswordController.PASSWORD_UPDATE_FORM));

        verifyZeroInteractions(passwordService);
    }

    @Test
    public void resetPassword() throws Exception {
        mockMvc.perform(post("/password/reset/update")
                   .param("password", PASSWORD)
                   .param("matchingPassword", PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(PasswordController.REDIRECT_LOGIN));

        ArgumentCaptor<PasswordCommand> passwordCommandAC =
                ArgumentCaptor.forClass(PasswordCommand.class);
        verify(passwordService).changePasswordForPrivilegedUser(
                passwordCommandAC.capture());
        PasswordCommand passwordCommand = passwordCommandAC.getValue();
        assertThat(passwordCommand.getPassword()).isEqualTo(PASSWORD);
        assertThat(passwordCommand.getMatchingPassword()).isEqualTo(PASSWORD);
    }
}