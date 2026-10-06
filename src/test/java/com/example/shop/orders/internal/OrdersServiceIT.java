package com.example.shop.orders.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.shop.TestcontainersConfiguration;
import com.example.shop.shared.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class OrdersServiceIT {

	@Autowired
	OrdersService orders;

	@Autowired
	JdbcTemplate jdbc;

	@Test
	void placeSucceedsAndReservesStock() {
		var productId = seedProduct("SKU-OK-1", 5);
		var orderId = orders.place(1L, List.of(new OrdersService.Line(productId, 2)));
		assertThat(orderId).isNotNull();
		assertThat(stockOf(productId)).isEqualTo(3);
	}

	@Test
	void placeFailsWhenStockInsufficient() {
		var productId = seedProduct("SKU-LOW-1", 1);
		assertThatThrownBy(() -> orders.place(1L, List.of(new OrdersService.Line(productId, 2))))
			.isInstanceOf(ConflictException.class);
		assertThat(stockOf(productId)).isEqualTo(1);
	}

	@Test
	void raceForLastUnitAllowsOnlyOneWinner() throws Exception {
		var productId = seedProduct("SKU-RACE-1", 1);
		var latch = new CountDownLatch(1);
		var successes = new AtomicInteger();
		var failures = new AtomicInteger();

		try (var pool = Executors.newFixedThreadPool(2)) {
			Future<?> first = pool.submit(() -> runPlace(productId, latch, successes, failures));
			Future<?> second = pool.submit(() -> runPlace(productId, latch, successes, failures));
			latch.countDown();
			first.get();
			second.get();
		}

		assertThat(successes.get()).isEqualTo(1);
		assertThat(failures.get()).isEqualTo(1);
		assertThat(stockOf(productId)).isEqualTo(0);
	}

	private void runPlace(Long productId, CountDownLatch latch, AtomicInteger successes, AtomicInteger failures) {
		try {
			latch.await();
			orders.place(42L, List.of(new OrdersService.Line(productId, 1)));
			successes.incrementAndGet();
		}
		catch (Exception ex) {
			failures.incrementAndGet();
		}
	}

	private Long seedProduct(String sku, int stock) {
		jdbc.update("""
			insert into products (sku, name, description, price_minor, stock, active)
			values (?, ?, 'test', 1000, ?, true)
			""", sku, sku, stock);
		return jdbc.queryForObject("select id from products where sku = ?", Long.class, sku);
	}

	private int stockOf(Long productId) {
		return jdbc.queryForObject("select stock from products where id = ?", Integer.class, productId);
	}
}
