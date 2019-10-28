package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.exceptions.EmailNotSentException;
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
        User user = User.builder().id(1L).firstName("Unmesh").lastName("Chowdhury")
                .email("unmesh@gmail.com").password("unmesh").build();
        String token = "erf56yj89j";

        service.createUserToken(user, token);

        verify(tokenService).createTokenForUser(user, token);
    }

    @Test
    public void getFullName() {
        String fullName = service.getFullName(User.builder().firstName("Unmesh")
                .lastName("Chowdhury").build());

        assertThat(fullName).isEqualTo("Unmesh Chowdhury");
    }

    @Test
    public void getServerNamePortContextPath() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("");
        String expectedValue = "http://localhost:8080";

        assertThat(service.getServerNamePortContextPath(request)).isEqualTo(expectedValue);
    }

    @Test
    public void getContext() {
        Context context = service.getContext("Unmesh Chowdhury",
                "http://localhost:8080/registration");

        assertThat(context.getVariable("name")).isEqualTo("Unmesh Chowdhury");
        assertThat(context.getVariable("url")).isEqualTo("http://localhost:8080/registration");
    }

    @Test
    public void sendLink() {
        MimeMessage message = Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);

        service.sendLink("unmesh@gmail.com", "Verification", "Click");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(message);
    }

    @Test(expected = EmailNotSentException.class)
    public void sendLinkException() {
        MimeMessage message = Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        doThrow(EmailNotSentException.class).when(mailSender).send(message);

        service.sendLink("unmesh@gmail.com", "Verification", "Click");

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(message);
    }

    @Test
    public void sendAccountActivationLink() {
        User user = User.builder().id(1L).firstName("Unmesh").lastName("Chowdhury")
                .email("unmesh@gmail.com").password("unmesh").build();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("");
        MimeMessage message = Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Subject");
        when(templateEngine.process(anyString(), any())).thenReturn("Body");

        service.sendAccountActivationLink(user, request);

        verify(mailSender).send(message);
    }

    @Test
    public void sendPasswordResetLink() {
        User user = User.builder().id(1L).firstName("Unmesh").lastName("Chowdhury")
                .email("unmeshchow@gmail.com").password("unmesh").build();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("");
        MimeMessage message = Mockito.mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Subject");
        when(templateEngine.process(anyString(), any())).thenReturn("Body");

        service.sendPasswordResetLink(user, request);

        verify(mailSender).send(message);
    }
}