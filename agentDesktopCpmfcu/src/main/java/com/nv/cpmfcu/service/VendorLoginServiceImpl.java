package com.nv.cpmfcu.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nv.cpmfcu.dto.SessionDataDto;
import com.nv.cpmfcu.exceptions.ExternalApiException;
import com.nv.cpmfcu.utility.XmlToJsonConverter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VendorLoginServiceImpl implements VendorLoginService {

	@Value("${cpmfcu.url}")
	private String url;

	@Value("${cpmfcu.vendorLoginRequestXML}")
	private String vendorLoginRequestXML;

	private final XmlToJsonConverter xmlToJsonConverter;

	private final RestTemplate restTemplate;

	private LocalDateTime lastAccessTime;

	public VendorLoginServiceImpl(XmlToJsonConverter xmlToJsonConverter, RestTemplate restTemplate) {
		this.xmlToJsonConverter = xmlToJsonConverter;
		this.restTemplate = restTemplate;
	}

	public SessionDataDto sendVendorLogin() {

		log.info("Inside VendorLoginServiceImpl class and Method name : sendVendorLogin() called");

		String xmlResponse = null;
		ResponseEntity<String> response = null;

		SessionDataDto sessionDataDto = null;

		log.info("XML Request body : {} ", vendorLoginRequestXML);

		try {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_XML);

			HttpEntity<String> requestEntity = new HttpEntity<>(vendorLoginRequestXML, httpHeaders);

			response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			xmlResponse = response.getBody();
			log.info("XML Response body : {} ", xmlResponse);

			// Converting the XML response to JSON
			JSONObject jsonResponse = xmlToJsonConverter.convertXmlToJson(xmlResponse);

			log.info("JSON Response body : {} ", jsonResponse);
			
			//JSONObject jsonObject = new JSONObject(jsonResponse);
			
			// Extracting the session ID from JSON response			
			JSONObject logonObject = jsonResponse.getJSONObject("query").getJSONObject("logon");
			String sessionId = logonObject.getString("sessionId");
     		Integer sessionTimeoutSeconds = logonObject.getInt("sessionTimeoutSeconds");
			
			sessionDataDto = new SessionDataDto();
			sessionDataDto.setSessionId(sessionId);
			sessionDataDto.setIdleTimeout(sessionTimeoutSeconds);

			log.info("Session ID : {} ", sessionId);
			log.info("Session Time out : {} ", sessionTimeoutSeconds);

		} catch (RestClientException e) {
			log.error("An error occurred while performing the vendor login: {}", e.getMessage(), e);
			e.printStackTrace();

		}
		catch (Exception e) {
			log.error("An error occurred while performing the vendor login: {}", e.getMessage(), e);
			e.printStackTrace();

		}

		return sessionDataDto;
	}

	@Cacheable("sessionCache")
	@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
	public SessionDataDto getSessionData() {

		log.info("Inside VendorLoginServiceImpl class and Method name : getSessionData() called");
		updateLastAccessTime();
		// String sessionDataDto = null;
		SessionDataDto sessionDataDto = null;
		try {
			sessionDataDto = sendVendorLogin();
		} catch (ExternalApiException e) {
			log.error("An error occurred while getting the session Data: {}", e.getMessage(), e);
			e.printStackTrace();
		} catch (Exception e) {
			log.error("An error occurred while getting the session Data: {}", e.getMessage(), e);
			e.printStackTrace();
		}

		return sessionDataDto;
	}

	private void updateLastAccessTime() {
		lastAccessTime = LocalDateTime.now();
	}

	@CacheEvict(value = "sessionCache", allEntries = true)
	public void cacheEvict() {
		log.info("Session has been idle, refreshing session data");
	}

	@Scheduled(fixedRate = 60000)
	public void refreshSessionIfIdle() {
		try {
			if (lastAccessTime != null) {
				long idleMinutes = ChronoUnit.MINUTES.between(lastAccessTime, LocalDateTime.now());
				log.info("Idle time in minutes: " + idleMinutes);
				if (idleMinutes >= 30) {
					log.info("Session has been idle for 30 mins, refreshing session data");
					cacheEvict();
					getSessionData(); // This will refresh the session data
				}
			}
		} catch (ExternalApiException e) {
			log.error("An error occurred while refreshing the session Data: {}", e.getMessage(), e);
			e.printStackTrace();
		} catch (Exception e) {
			log.error("An error occurred while refreshing the session Data: {}", e.getMessage(), e);
			e.printStackTrace();
		}
	}

}
