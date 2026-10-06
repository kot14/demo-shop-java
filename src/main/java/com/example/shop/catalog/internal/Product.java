package com.example.shop.catalog.internal;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 64)
	private String sku;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "price_minor", nullable = false)
	private long priceMinor;

	@Column(nullable = false)
	private int stock;

	@Column(nullable = false)
	private boolean active = true;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected Product() {
	}

	Product(String sku, String name, String description, long priceMinor, int stock) {
		this.sku = sku;
		this.name = name;
		this.description = description;
		this.priceMinor = priceMinor;
		this.stock = stock;
	}

	Long getId() {
		return id;
	}

	String getSku() {
		return sku;
	}

	String getName() {
		return name;
	}

	String getDescription() {
		return description;
	}

	long getPriceMinor() {
		return priceMinor;
	}

	int getStock() {
		return stock;
	}

	boolean isActive() {
		return active;
	}

	void update(String name, String description, long priceMinor, int stock, boolean active) {
		this.name = name;
		this.description = description;
		this.priceMinor = priceMinor;
		this.stock = stock;
		this.active = active;
	}

	void deactivate() {
		this.active = false;
	}
}
