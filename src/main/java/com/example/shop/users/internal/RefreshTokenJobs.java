package com.example.shop.users.internal;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class RefreshTokenJobs {

	private static final Logger log = LoggerFactory.getLogger(RefreshTokenJobs.class);

	private final RefreshTokenRepository refreshTokens;

	RefreshTokenJobs(RefreshTokenRepository refreshTokens) {
		this.refreshTokens = refreshTokens;
	}

	@Scheduled(cron = "0 0 4 * * *")
	@Transactional
	void purgeExpiredRefreshTokens() {
		var before = Instant.now();
		refreshTokens.deleteByExpiresAtBefore(before);
		log.info("Purged expired refresh tokens before {}", before);
	}
}
