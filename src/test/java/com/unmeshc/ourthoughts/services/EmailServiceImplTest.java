package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.exceptions.EmailNotSentException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import static com.unmeshc.ourthoughts.TestLiterals.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ITemplateEngine templateEngine;

    @Mock
    private MessageSource messageSource;

    @Mock
    private VerificationTokenService tokenService;

    @InjectMocks
    private EmailServiceImpl service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getRandomToken() {
        String token1 = service.getRandomToken();
        String token2 = service.getRandomToken();

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    public void createUserToken() {
        User user = User.builder().id(ID).firstName(FIRST_NAME).lastName(LAST_NAME)
                .email(EMAIL).password(PASSWORD).build();

        service.createUserVerificationToken(user, TOKEN);

        verify(tokenService).createVerificationTokenForUser(user, TOKEN);
    }

    @Test
    public void getFullName() {
        String fullName = service.getFullName(User.builder().firstName(FIRST_NAME)
                .lastName(LAST_NAME).build());

        assertThat(fullName).isEqualTo(FULL_NAME);
    }

    @Test
    public void getServerNamePortContextPath() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getServerName()).thenReturn(LOCALHOST);
        when(request.getServerPort()).thenReturn(PORT);
        when(request.getContextPath()).thenReturn(EMPTY);
        String expectedValue = "http://localhost:8080";

        assertThat(service.getServerNamePortAndContextPath(request)).isEqualTo(expectedValue);
    }

    @Test
    public void getContext() {
        Context context = service.getContext(FULL_NAME,
                "http://localhost:8080/registration");

        assertThat(context.getVariable("name")).isEqualTo(FULL_NAME);
        assertThat(context.getVariable("url"))
                .isEqualTo("http://localhost:8080/registration");
    }

    @Test
    public void sendLink() {
        MimeMessage message = Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);

        service.sendLink(EMAIL, VERIFICATION, CLICK);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(message);
    }

    @Test(expected = EmailNotSentException.class)
    public void sendLinkException() {
        MimeMessage message = Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        doThrow(EmailNotSentException.class).when(mailSender).send(message);

        service.sendLink(EMAIL, VERIFICATION, CLICK);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(message);
    }

    @Test
    public void sendAccountActivationLink() {
        User user = User.builder().id(ID).firstName(FIRST_NAME).lastName(LAST_NAME)
                .email(EMAIL).password(PASSWORD).build();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getServerName()).thenReturn(LOCALHOST);
        when(request.getServerPort()).thenReturn(PORT);
        when(request.getContextPath()).thenReturn(EMPTY);
        MimeMessage message = Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(messageSource.getMessage(any(), any(), any())).thenReturn(SUBJECT);
        when(templateEngine.process(anyString(), any())).thenReturn(BODY);

        service.sendAccountActivationLinkForUser(user, request);

        verify(mailSender).send(message);
    }

    @Test
    public void sendPasswordResetLink() {
        User user = User.builder().id(ID).firstName(FIRST_NAME).lastName(LAST_NAME)
                .email(EMAIL).password(PASSWORD).build();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getServerName()).thenReturn(LOCALHOST);
        when(request.getServerPort()).thenReturn(PORT);
        when(request.getContextPath()).thenReturn(EMPTY);
        MimeMessage message = Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(messageSource.getMessage(any(), any(), any())).thenReturn(SUBJECT);
        when(templateEngine.process(anyString(), any())).thenReturn(BODY);

        service.sendPasswordResetLinkForUser(user, request);

        verify(mailSender).send(message);
    }
}