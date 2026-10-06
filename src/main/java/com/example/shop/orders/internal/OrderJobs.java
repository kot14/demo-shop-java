package com.example.shop.orders.internal;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class OrderJobs {

	private static final Logger log = LoggerFactory.getLogger(OrderJobs.class);
	private static final ZoneId KYIV = ZoneId.of("Europe/Kyiv");

	private final OrdersService service;

	OrderJobs(OrdersService service) {
		this.service = service;
	}

	@Scheduled(cron = "0 */15 * * * *")
	void cancelUnpaidOrders() {
		service.cancelUnpaid(Duration.ofMinutes(30));
		log.info("Cancel unpaid orders job finished");
	}

	@Scheduled(cron = "0 30 2 * * *", zone = "Europe/Kyiv")
	void nightlySalesReport() {
		var yesterday = LocalDate.now(KYIV).minusDays(1);
		var from = yesterday.atStartOfDay(KYIV).toInstant();
		var to = yesterday.plusDays(1).atStartOfDay(KYIV).toInstant();
		var total = service.paidTotal(from, to);
		log.info("Nightly sales report for {}: total_minor={}", yesterday, total);
	}
}
