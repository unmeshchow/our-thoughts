package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.services.exceptions.EmailNotSentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
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
    private final ITemplateEngine templateEngine;
    private final MessageSource messageSource;
    private final VerificationTokenService verificationTokenService;

    public EmailServiceImpl(JavaMailSender mailSender,
                            ITemplateEngine templateEngine,
                            MessageSource messageSource,
                            VerificationTokenService verificationTokenService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
        this.verificationTokenService = verificationTokenService;
    }

    @Override
    public void sendAccountActivationLinkForUser(User user, HttpServletRequest request) {
        String token = getRandomToken();
        createUserVerificationToken(user, token);

        String fullName = getFullName(user);
        String url = getServerNamePortAndContextPath(request) +
                "/registration/confirm?token=" + token;

        Context context = getContext(fullName, url);

        String subject = messageSource.getMessage("activate.account.link.subject",
                null, request.getLocale());
        String body = templateEngine.process(
                "email/activateAccountLink", context);

        sendLink(user.getEmail(), subject, body);
    }

    @Override
    public void sendPasswordResetLinkForUser(User user, HttpServletRequest request) {
        String token = getRandomToken();
        createUserVerificationToken(user, token);

        String fullName = getFullName(user);
        String url = getServerNamePortAndContextPath(request) +
                "/password/reset/confirm?token=" + token;

        Context context = getContext(fullName, url);

        String subject = messageSource.getMessage("password.reset.link.subject",
                null, request.getLocale());
        String body = templateEngine.process(
                "email/passwordResetLink", context);

        sendLink(user.getEmail(), subject, body);
    }

    void sendLink(String to, String subject, String body) {
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

    String getRandomToken() {
        return UUID.randomUUID().toString();
    }

    void createUserVerificationToken(User user, String token) {
        verificationTokenService.createVerificationTokenForUser(user, token);
    }

    String getFullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    String getServerNamePortAndContextPath(HttpServletRequest request) {
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();

        return "http://" + serverName + ":" + port + contextPath;
    }

    Context getContext(String fullName, String url) {
        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("name", fullName);
        context.setVariable("url", url);

        return context;
    }
}