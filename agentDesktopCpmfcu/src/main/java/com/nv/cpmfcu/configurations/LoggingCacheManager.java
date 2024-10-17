package com.nv.cpmfcu.configurations;


import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingCacheManager extends ConcurrentMapCacheManager {

    @Override
    protected Cache createConcurrentMapCache(final String name) {
        return new LoggingCache(name);
    }

    private static class LoggingCache extends ConcurrentMapCache {

        public LoggingCache(String name) {
            super(name);
        }

        @Override
        public ValueWrapper get(Object key) {
            ValueWrapper value = super.get(key);
            if (value != null) {
                log.info("Cache hit for key: " + key);
            } else {
                log.info("Cache miss for key: " + key);
            }
            return value;
        }

        @Override
        public void put(Object key, Object value) {
            log.info("Cache put for key: " + key);
            super.put(key, value);
        }

        @Override
        public void evict(Object key) {
            log.info("Cache evict for key: " + key);
            super.evict(key);
        }
    }
}
