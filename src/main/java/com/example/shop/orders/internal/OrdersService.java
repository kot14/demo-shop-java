package com.example.shop.orders.internal;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.example.shop.catalog.CatalogApi;
import com.example.shop.orders.OrderPlaced;
import com.example.shop.shared.ConflictException;
import com.example.shop.shared.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class OrdersService {

	private final CustomerOrderRepository orders;
	private final CatalogApi catalog;
	private final ApplicationEventPublisher events;

	OrdersService(CustomerOrderRepository orders, CatalogApi catalog, ApplicationEventPublisher events) {
		this.orders = orders;
		this.catalog = catalog;
		this.events = events;
	}

	@Transactional
	public Long place(Long customerId, List<Line> lines) {
		if (lines == null || lines.isEmpty()) {
			throw new IllegalArgumentException("Замовлення не може бути порожнім");
		}
		var order = new CustomerOrder(customerId);
		for (var line : lines) {
			var product = catalog.findActive(line.productId())
				.orElseThrow(() -> new IllegalArgumentException("Товар не знайдено"));
			if (!catalog.reserveStock(product.id(), line.quantity())) {
				throw new ConflictException("Недостатньо товару: " + product.name());
			}
			order.addItem(product, line.quantity());
		}
		orders.save(order);
		events.publishEvent(new OrderPlaced(order.getId(), customerId));
		return order.getId();
	}

	@Transactional(readOnly = true)
	List<OrderView> myOrders(Long customerId) {
		return orders.findByCustomerIdOrderByCreatedAtDesc(customerId).stream().map(OrderView::from).toList();
	}

	@Transactional(readOnly = true)
	OrderView getMine(Long customerId, Long orderId) {
		return orders.findByIdAndCustomerId(orderId, customerId)
			.map(OrderView::from)
			.orElseThrow(() -> new NotFoundException("Замовлення не знайдено"));
	}

	@Transactional
	public void cancelUnpaid(Duration olderThan) {
		var cutoff = Instant.now().minus(olderThan);
		orders.findByStatusAndCreatedAtBefore(OrderStatus.NEW, cutoff).forEach(order -> {
			order.cancel();
			order.getItems().forEach(item -> catalog.releaseStock(item.getProductId(), item.getQuantity()));
		});
	}

	long paidTotal(Instant from, Instant to) {
		return orders.sumPaidBetween(from, to);
	}

	record Line(Long productId, int quantity) {
	}

	record OrderItemView(Long productId, String productName, long unitPriceMinor, int quantity) {
	}

	record OrderView(Long id, Long customerId, OrderStatus status, long totalMinor, Instant createdAt, Instant paidAt, List<OrderItemView> items) {
		static OrderView from(CustomerOrder order) {
			var items = order.getItems().stream()
				.map(i -> new OrderItemView(i.getProductId(), i.getProductName(), i.getUnitPriceMinor(), i.getQuantity()))
				.toList();
			return new OrderView(
				order.getId(),
				order.getCustomerId(),
				order.getStatus(),
				order.getTotalMinor(),
				order.getCreatedAt(),
				order.getPaidAt(),
				items
			);
		}
	}
}
