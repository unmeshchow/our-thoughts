package com.unmeshc.ourthoughts.configurations;

import com.unmeshc.ourthoughts.services.VerificationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by uc on 11/3/2019
 */
@Slf4j
@Configuration
@EnableScheduling
public class TaskConfiguration {

    private final VerificationTokenService verificationTokenService;

    public TaskConfiguration(VerificationTokenService verificationTokenService) {
        this.verificationTokenService = verificationTokenService;
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void deleteExpiredToken() {
        log.debug("Expired verification tokens are deleting ...");
        verificationTokenService.deleteExpiredTokens();
    }
}
