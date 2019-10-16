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
    private final UserService userService;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    public EmailServiceImpl(JavaMailSender mailSender,
                            UserService userService,
                            TemplateEngine templateEngine,
                            MessageSource messageSource) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    @Override
    public void sendAccountActivateLink(User user, HttpServletRequest request) {
        String token = UUID.randomUUID().toString();
        userService.createToken(user, token);

        String fullName = user.getFirstName() + " " + user.getLastName();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();
        String url = "http://" + serverName + ":" + port + contextPath +
                "/registration/confirm?token=" + token;

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("name", fullName);
        context.setVariable("url", url);

        String subject = messageSource.getMessage("activate.account.link.subject",
                null, request.getLocale());
        String body = templateEngine.process(
                "email/activateAccountLink", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
        } catch (Exception exc) {
            log.error("Error in sending activation email", exc);
            throw new EmailNotSentException();
        }
    }
}
