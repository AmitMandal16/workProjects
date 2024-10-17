package com.nv.gulfstream.schedular.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchJobSchedulerConfig {

    @Value("${job.schedule.cron}")
    private String cronExpression;

    @Value("${job.schedule.timezone}")
    private String timezone;

    @Bean
    String cronExpression() {
        return cronExpression;
    }

    @Bean
    String timezone() {
        return timezone;
    }
}
