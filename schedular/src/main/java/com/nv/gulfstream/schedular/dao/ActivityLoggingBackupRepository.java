package com.nv.gulfstream.schedular.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nv.gulfstream.schedular.entity.ActivityLoggingBackup;

public interface ActivityLoggingBackupRepository extends JpaRepository<ActivityLoggingBackup, Long> {

	@Query("SELECT a FROM ActivityLoggingBackup a WHERE a.modifiedDateTime >= :sixMonthsAgo")
	List<ActivityLoggingBackup> findEntriesFromLastSixMonths(@Param("sixMonthsAgo") LocalDateTime sixMonthsAgo);

	void deleteByIdIn(List<Long> ids);

}
