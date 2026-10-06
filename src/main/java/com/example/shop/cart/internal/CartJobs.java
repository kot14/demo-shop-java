package com.example.shop.cart.internal;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class CartJobs {

	private static final Logger log = LoggerFactory.getLogger(CartJobs.class);

	private final CartService carts;

	CartJobs(CartService carts) {
		this.carts = carts;
	}

	@Scheduled(cron = "0 0 3 * * *", zone = "Europe/Kyiv")
	@Transactional
	void purgeAbandonedCarts() {
		var cutoff = Instant.now().minus(Duration.ofDays(30));
		carts.purgeAbandoned(cutoff);
		log.info("Purged abandoned carts older than {}", cutoff);
	}
}
