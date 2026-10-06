package com.example.shop.orders.internal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.shop.catalog.ProductInfo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
class CustomerOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_id", nullable = false)
	private Long customerId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status = OrderStatus.NEW;

	@Column(name = "total_minor", nullable = false)
	private long totalMinor;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	@Column(name = "paid_at")
	private Instant paidAt;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	protected CustomerOrder() {
	}

	CustomerOrder(Long customerId) {
		this.customerId = customerId;
	}

	Long getId() {
		return id;
	}

	Long getCustomerId() {
		return customerId;
	}

	OrderStatus getStatus() {
		return status;
	}

	long getTotalMinor() {
		return totalMinor;
	}

	Instant getCreatedAt() {
		return createdAt;
	}

	Instant getPaidAt() {
		return paidAt;
	}

	List<OrderItem> getItems() {
		return items;
	}

	void addItem(ProductInfo product, int qty) {
		items.add(new OrderItem(this, product.id(), product.name(), product.priceMinor(), qty));
		totalMinor += product.priceMinor() * qty;
	}

	void cancel() {
		this.status = OrderStatus.CANCELLED;
	}
}
