package com.nv.cpmfcu.configurations;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class CacheConfig {
	
	  @Bean
	  @Primary
	  CacheManager cacheManager() { 		  
		  return new LoggingCacheManager();		  
	  } 
}
