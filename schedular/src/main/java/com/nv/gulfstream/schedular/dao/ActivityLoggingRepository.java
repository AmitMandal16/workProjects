package com.nv.gulfstream.schedular.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nv.gulfstream.schedular.entity.ActivityLogging;

public interface ActivityLoggingRepository extends JpaRepository<ActivityLogging, Long>{

}
