package com.nv.gulfstream.schedular.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BatchJobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobScheduler.class);

    @Autowired
    private ActivityLoggingService activityLoggingService;

 
    @Value("${job.schedule.cron}")
    private String cronExpression;

    @Value("${job.schedule.timezone}")
    private String timezone;

   
    @Scheduled(cron = "#{@batchJobScheduler.cronExpression}", zone = "#{@batchJobScheduler.timezone}")
    public void runScheduledJob() {
        logger.info("Scheduled job started at 4 AM EST.");

        try {
           
            activityLoggingService.transferAndDeleteOldEntries();
        } catch (Exception e) {
            logger.error("The job failed after retry attempts: {}", e.getMessage(), e);
        }

        logger.info("Scheduled job finished.");
    }
}
