package com.example.shop.shared;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
class CacheConfig {

	@Bean
	CacheManager cacheManager() {
		var manager = new CaffeineCacheManager("products", "categories");
		manager.setCaffeine(Caffeine.newBuilder()
			.maximumSize(10_000)
			.expireAfterWrite(Duration.ofMinutes(10))
			.recordStats());
		return manager;
	}
}
