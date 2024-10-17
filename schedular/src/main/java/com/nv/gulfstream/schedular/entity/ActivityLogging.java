package com.nv.gulfstream.schedular.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ActivityLogging")
@Data
public class ActivityLogging {
	
	@Id
    private Long id;
    private LocalDateTime modifiedDateTime;
    private String actionType;
    private String agentId;
    private String value;
    private String type;
    private String name;
    private Long categoryId;
    private Boolean isFav;
    private String pickEmail;
    private Boolean isPublic;
    
}
