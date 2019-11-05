package com.unmeshc.ourthoughts.configurations;

import com.unmeshc.ourthoughts.services.TaskService;
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

    private final TaskService taskService;

    public TaskConfiguration(TaskService taskService) {
        this.taskService = taskService;
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void deleteExpiredToken() {
        log.debug("Deleting expired tokens and inactive users ...");
        taskService.deleteExpiredTokensAndInactiveUsers();
        log.debug("Deletion has completed");
    }
}
