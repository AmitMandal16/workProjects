package com.nv.gulfstream.schedular.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.nv.gulfstream.schedular.dao.ActivityLoggingBackupRepository;
import com.nv.gulfstream.schedular.dao.ActivityLoggingRepository;
import com.nv.gulfstream.schedular.entity.ActivityLogging;
import com.nv.gulfstream.schedular.entity.ActivityLoggingBackup;

import jakarta.transaction.Transactional;

@Service
public class ActivityLoggingService {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivityLoggingService.class);

    @Autowired
    private ActivityLoggingBackupRepository backupRepository;

    @Autowired
    private ActivityLoggingRepository loggingRepository;

    @Retryable(
            include = { Exception.class }, // Retry for any kind of Exception
            maxAttempts = 3,               // Retry up to 3 times
            backoff = @Backoff(delay = 5000) // 5-second delay between retries
        )
    @Transactional
    public void transferAndDeleteOldEntries() {
    	
        // Calculate the date 6 months ago
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        // Fetch data from ActivityLoggingBackup
        List<ActivityLoggingBackup> backups = backupRepository.findEntriesFromLastSixMonths(sixMonthsAgo);

        //No data to transfer
        if (backups.isEmpty()) {
            logger.info("No records found in ActivityLoggingBackup from the last 6 months.");
            return;
        }

        // Map the data to ActivityLogging
        List<ActivityLogging> logs = backups.stream().map(backup -> {
           
        	ActivityLogging log = new ActivityLogging();
            log.setId(backup.getId()); // Ensure unique ids for ActivityLogging
            log.setModifiedDateTime(backup.getModifiedDateTime());
            log.setActionType(backup.getActionType());
            log.setAgentId(backup.getAgentId());
            log.setValue(backup.getValue());
            log.setType(backup.getType());
            log.setName(backup.getName());
            log.setCategoryId(backup.getCategoryId());
            log.setIsFav(backup.getIsFav());
            log.setPickEmail(backup.getPickEmail());
            log.setIsPublic(backup.getIsPublic());
            return log;
            
        }).collect(Collectors.toList());

        try {
            // Insert data into ActivityLogging
            loggingRepository.saveAll(logs);

            // Log successful transfer
            logger.info("Successfully inserted {} records into ActivityLogging.", logs.size());

            // Get IDs of records to delete from ActivityLoggingBackup
            List<Long> backupIds = backups.stream().map(ActivityLoggingBackup::getId).collect(Collectors.toList());

            // Delete transferred records from ActivityLoggingBackup
            backupRepository.deleteByIdIn(backupIds);

            // Log successful deletion
            logger.info("Successfully deleted {} records from ActivityLoggingBackup.", backupIds.size());
        } 
        catch (Exception e) {
            logger.error("Error occurred during the transfer and deletion process: {}", e.getMessage(), e);
           throw e; // Rethrow the exception to trigger rollback
           
        }
    }
    
    
    // This method will be called if the retry attempts fail
    @Recover
    public void recover(Exception e) {
        logger.error("Failed to transfer and delete entries after retries. Manual intervention may be required.", e);
    }

}
