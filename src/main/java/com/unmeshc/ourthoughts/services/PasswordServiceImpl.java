package com.unmeshc.ourthoughts.services;

import com.unmeshc.ourthoughts.commands.PasswordCommand;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.domain.VerificationToken;
import com.unmeshc.ourthoughts.services.exceptions.BadVerificationTokenException;
import com.unmeshc.ourthoughts.services.exceptions.NotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created by uc on 10/19/2019
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    private final EmailService emailService;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;

    public PasswordServiceImpl(EmailService emailService,
                               UserService userService,
                               VerificationTokenService verificationTokenService,
                               PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void verifyResetPasswordForUserByEmailing(String email, HttpServletRequest request) {
        User user = userService.getUserByEmail(email);
        if (user.isAdmin() || !user.getActive()) {
            throw new NotFoundException("User not found with email: " + email);
        }
        emailService.sendPasswordResetLinkForUser(user, request);
    }

    @Override
    public void checkAndSetChangePasswordPrivilege(String token)
            throws BadVerificationTokenException {

        VerificationToken foundToken =
                verificationTokenService.getVerificationTokenByToken(token);
        if (foundToken == null || foundToken.isExpired()) {
            throw new BadVerificationTokenException();
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(foundToken.getUser(),
                null, Arrays.asList(new SimpleGrantedAuthority(
                        "CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public void changePasswordForPrivilegedUser(PasswordCommand passwordCommand) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setPassword(passwordEncoder.encode(passwordCommand.getPassword()));
        userService.saveOrUpdateUser(user);
    }
}