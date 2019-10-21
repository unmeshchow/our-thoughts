package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.domain.Token;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.PasswordService;
import com.unmeshc.ourthoughts.services.TokenService;
import com.unmeshc.ourthoughts.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PasswordControllerTest {

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private PasswordController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void showPasswordResetForm() throws Exception {
        mockMvc.perform(get("/password/reset/form"))
               .andExpect(status().isOk())
               .andExpect(view().name(PasswordController.PASSWORD_RESET_FORM));
    }

    @Test
    public void processPasswordReset() throws Exception {
        User user = User.builder().id(1L).active(true).build();
        when(userService.getByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/password/reset/send")
                   .param("email", "unmesh@gmail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(PasswordController.REDIRECT_PASSWORD_RESET_SUCCESS));

        ArgumentCaptor<User> userAC = ArgumentCaptor.forClass(User.class);
        verify(passwordService).verifyResetPassword(userAC.capture(), any());
        User usedUser = userAC.getValue();
        assertThat(usedUser.getActive()).isEqualTo(user.getActive());
    }

    @Test(expected = NestedServletException.class)
    public void processPasswordResetNull() throws Exception {
        when(userService.getByEmail(anyString())).thenReturn(null);

        mockMvc.perform(get("/password/reset/send")
                .param("email", "unmesh@gmail.com"));
    }

    @Test(expected = NestedServletException.class)
    public void processPasswordResetInactive() throws Exception {
        User user = User.builder().id(1L).active(false).build();
        when(userService.getByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/password/reset/send")
                .param("email", "unmesh@gmail.com"));
    }

    @Test
    public void passwordResetSuccess() throws Exception {
        mockMvc.perform(get("/password/reset/success"))
               .andExpect(status().isOk())
               .andExpect(view().name(PasswordController.PASSWORD_RESET_SUCCESS));
    }

    @Test
    public void acceptPasswordResetNull() throws Exception {
        when(tokenService.getByToken(anyString())).thenReturn(null);

        mockMvc.perform(get("/password/reset/confirm")
                   .param("token", "er457hy78"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name(PasswordController.REDIRECT_PASSWORD_RESET_CONFIRM_BAD));
    }

    @Test
    public void acceptPasswordResetExpired() throws Exception {
        Token token = Token.builder().id(1L).expiryDate(LocalDateTime.now().minusDays(1L)).build();
        when(tokenService.getByToken(anyString())).thenReturn(token);

        mockMvc.perform(get("/password/reset/confirm")
                .param("token", "er457hy78"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(PasswordController.REDIRECT_PASSWORD_RESET_CONFIRM_BAD));
    }

    @Test
    public void acceptPasswordReset() throws Exception {
        Token token = Token.builder().id(1L).expiryDate(LocalDateTime.now().plusDays(1L)).build();
        when(tokenService.getByToken(anyString())).thenReturn(token);

        mockMvc.perform(get("/password/reset/confirm")
                .param("token", "er457hy78"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(PasswordController.REDIRECT_PASSWORD_RESET_UPDATE_FORM));
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
                   .param("password", "unmesh")
                   .param("matchingPassword", "unmeshc"))
               .andExpect(status().isOk())
               .andExpect(model().errorCount(1))
               .andExpect(view().name(PasswordController.PASSWORD_UPDATE_FORM));

        verifyZeroInteractions(passwordService);
    }

    @Test
    public void resetPassword() throws Exception {
        User user = User.builder().id(1L).build();
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        mockMvc.perform(post("/password/reset/update")
                   .param("password", "unmesh")
                   .param("matchingPassword", "unmesh"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(PasswordController.REDIRECT_LOGIN));

        ArgumentCaptor<PasswordCommand> passwordCommandAC = ArgumentCaptor.forClass(PasswordCommand.class);
        verify(passwordService).updatePassword(any(), passwordCommandAC.capture());
        PasswordCommand passwordCommand = passwordCommandAC.getValue();
        assertThat(passwordCommand.getPassword()).isEqualTo("unmesh");
        assertThat(passwordCommand.getMatchingPassword()).isEqualTo("unmesh");
    }
}