package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.exceptions.EmailNotSentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Created by uc on 10/16/2019
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final TokenService tokenService;

    public EmailServiceImpl(JavaMailSender mailSender,
                            TemplateEngine templateEngine,
                            MessageSource messageSource,
                            TokenService tokenService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
        this.tokenService = tokenService;
    }

    @Override
    public void sendAccountActivationLink(User user, HttpServletRequest request) {
        String token = getRandomToken();
        createUserToken(user, token);

        String fullName = getFullName(user);
        String url = getServerNamePortContextPath(request) +
                "/registration/confirm?token=" + token;

        Context context = getContext(fullName, url);

        String subject = messageSource.getMessage("activate.account.link.subject",
                null, request.getLocale());
        String body = templateEngine.process(
                "email/activateAccountLink", context);

        sendLink(user.getEmail(), subject, body);
    }

    @Override
    public void sendPasswordResetLink(User user, HttpServletRequest request) {
        String token = getRandomToken();
        createUserToken(user, token);

        String fullName = getFullName(user);
        String url = getServerNamePortContextPath(request) +
                "/password/reset/confirm?token=" + token;

        Context context = getContext(fullName, url);

        String subject = messageSource.getMessage("password.reset.link.subject",
                null, request.getLocale());
        String body = templateEngine.process(
                "email/passwordResetLink", context);

        sendLink(user.getEmail(), subject, body);
    }

    private void sendLink(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

        } catch (Exception exc) {
            log.error("Error in sending activation email", exc);
            throw new EmailNotSentException("Email is not sent, try again.");
        }
    }

    private String getRandomToken() {
        return UUID.randomUUID().toString();
    }

    private void createUserToken(User user, String token) {
        tokenService.createTokenForUser(user, token);
    }

    private String getFullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private String getServerNamePortContextPath(HttpServletRequest request) {
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();

        return "http://" + serverName + ":" + port + contextPath;
    }

    private Context getContext(String fullName, String url) {
        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("name", fullName);
        context.setVariable("url", url);

        return context;
    }
}