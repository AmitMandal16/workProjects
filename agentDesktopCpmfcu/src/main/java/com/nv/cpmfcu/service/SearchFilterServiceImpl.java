package com.nv.cpmfcu.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nv.cpmfcu.dto.PersonDataDto;
import com.nv.cpmfcu.dto.SearchRequestDto;
import com.nv.cpmfcu.dto.SessionDataDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SearchFilterServiceImpl implements SearchFilterService {

	private final RestTemplate restTemplate;

	@Autowired
	private VendorLoginService vendorLoginService;

	@Value("${cpmfcu.url}")
	private String url;

	@Value("${cpmfcu.searchRequestXML}")
	private String searchRequestXML;

	public SearchFilterServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String replaceValues(String xmlRequest, SearchRequestDto searchRequestDto) {

		log.info("Method name : replaceValues() called..");

		SessionDataDto sessionDataDto = vendorLoginService.getSessionData();
		String sessionId = sessionDataDto != null ? sessionDataDto.getSessionId() : null;
		// String sessionId = vendorLoginService.getSessionData();

		Map<String, String> map = new HashMap<>();

		map.put("sessionId", sessionId);
		map.put("tableName", searchRequestDto.getTableName());
		map.put("filterName", searchRequestDto.getFilterName());
		map.put("columnName", searchRequestDto.getColumnName());
		map.put("content", searchRequestDto.getContent());

		String replacedValues = xmlRequest;

		for (Map.Entry<String, String> entry : map.entrySet()) {
			replacedValues = replacedValues.replace("#" + entry.getKey() + "#", entry.getValue());
		}

		return replacedValues;
	}

	public PersonDataDto sendSearchRequest(SearchRequestDto searchRequestDto) {

		log.info("Method name : sendSearchRequest() called..");

		String xmlResponse = null;
		ResponseEntity<String> response = null;
		JSONObject jsonObject = null;

		PersonDataDto personDataDto = new PersonDataDto();

		try {

			String xmlBody = replaceValues(searchRequestXML, searchRequestDto);

			log.debug("XML Request body : {} ", xmlBody);

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_XML);

			HttpEntity<String> requestEntity = new HttpEntity<>(xmlBody, httpHeaders);

			response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			xmlResponse = response.getBody();

			log.debug("XML Response body : {} ", xmlResponse);

			jsonObject = XML.toJSONObject(xmlResponse);

			JSONObject jsonTransactionObject = jsonObject.getJSONObject("query").getJSONObject("sequence")
					.getJSONObject("transaction");

			JSONArray steps = jsonTransactionObject.getJSONArray("step");

			steps.forEach(stepObject -> {
				JSONObject step = (JSONObject) stepObject;
				if (step.has("record")) {
					JSONArray fields = step.getJSONObject("record").getJSONArray("field");

					fields.forEach(fieldObject -> {
						JSONObject field = (JSONObject) fieldObject;
						String columnName = field.getString("columnName");
						Object newContents = field.opt("newContents");

						mapFieldToPersonData(personDataDto, columnName, newContents);
					});
				}
			});

			log.debug("JSON Response body : {} ", personDataDto);

		} catch (RestClientException e) {
			log.error("An error occurred while performing sendSearchRequest: {}", e.getMessage(), e);
			e.printStackTrace();
		}
		return personDataDto;
	}

	private void mapFieldToPersonData(PersonDataDto personDataDto, String columnName, Object newContents) {
		switch (columnName.toUpperCase()) {
		case "FIRST_NAME":
			personDataDto.setFirstName((String) newContents);
			break;
		case "MIDDLE_NAME":
			personDataDto.setMiddleName((String) newContents);
			break;
		case "LAST_NAME":
			personDataDto.setLastName((String) newContents);
			break;
		case "BIRTH_DATE":
			personDataDto.setBirthDate((String) newContents);
			break;
		case "TIN":
			personDataDto.setTin((newContents != null) ? Integer.parseInt(newContents.toString()) : null);
			break;
		//default:
		//	log.warn("Unhandled column: {}", columnName);
		}
	}

}
