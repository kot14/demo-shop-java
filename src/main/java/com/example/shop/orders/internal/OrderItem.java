package com.example.shop.orders.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private CustomerOrder order;

	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Column(name = "product_name", nullable = false)
	private String productName;

	@Column(name = "unit_price_minor", nullable = false)
	private long unitPriceMinor;

	@Column(nullable = false)
	private int quantity;

	protected OrderItem() {
	}

	OrderItem(CustomerOrder order, Long productId, String productName, long unitPriceMinor, int quantity) {
		this.order = order;
		this.productId = productId;
		this.productName = productName;
		this.unitPriceMinor = unitPriceMinor;
		this.quantity = quantity;
	}

	Long getProductId() {
		return productId;
	}

	String getProductName() {
		return productName;
	}

	long getUnitPriceMinor() {
		return unitPriceMinor;
	}

	int getQuantity() {
		return quantity;
	}
}
