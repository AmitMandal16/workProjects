package com.nv.cpmfcu.service;

import com.nv.cpmfcu.dto.PersonDataDto;
import com.nv.cpmfcu.dto.SearchRequestDto;

public interface SearchFilterService {
	
	public PersonDataDto sendSearchRequest(SearchRequestDto searchRequestDto);

}
