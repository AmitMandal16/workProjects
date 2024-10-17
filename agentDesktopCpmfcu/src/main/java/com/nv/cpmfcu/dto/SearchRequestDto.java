package com.nv.cpmfcu.dto;

import lombok.Data;

@Data
public class SearchRequestDto {
	
	private String tableName;
	private String filterName;
	private String columnName;
	private String content;

}
