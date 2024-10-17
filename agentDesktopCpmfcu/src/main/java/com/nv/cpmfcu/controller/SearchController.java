package com.nv.cpmfcu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nv.cpmfcu.dto.PersonDataDto;
import com.nv.cpmfcu.dto.SearchRequestDto;
import com.nv.cpmfcu.service.SearchFilterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/cpmfcu")
@Slf4j
public class SearchController {

	private SearchFilterService searchFilterService;

	public SearchController(SearchFilterService searchFilterService) {
		this.searchFilterService = searchFilterService;
	}

	@PostMapping("/searchFilterRequest")
	public ResponseEntity<PersonDataDto> searchFilterRequest(@RequestBody SearchRequestDto searchRequestDto) {
		PersonDataDto personDataDto = null;
		    try {
		    	personDataDto = searchFilterService.sendSearchRequest(searchRequestDto);
			    log.info("Inside SearchController and Method name: searchFilterRequest() JSON Response: {} ", personDataDto);
			    log.info("Inside SearchController and Method name: searchFilterRequest() called.. ");
			} catch (Exception e) {
			    log.error("Error processing search request: ", e);
			   
			}

		    return ResponseEntity.ok().body(personDataDto);
	}

}
