package com.example.shop.cart.internal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "carts")
class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_id", nullable = false, unique = true)
	private Long customerId;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt = Instant.now();

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>();

	protected Cart() {
	}

	Cart(Long customerId) {
		this.customerId = customerId;
	}

	Long getId() {
		return id;
	}

	Long getCustomerId() {
		return customerId;
	}

	Instant getUpdatedAt() {
		return updatedAt;
	}

	List<CartItem> getItems() {
		return items;
	}

	void touch() {
		this.updatedAt = Instant.now();
	}

	void upsertItem(Long productId, int quantity) {
		var existing = findItem(productId);
		if (existing.isPresent()) {
			existing.get().setQuantity(quantity);
		}
		else {
			items.add(new CartItem(this, productId, quantity));
		}
		touch();
	}

	void removeItem(Long productId) {
		items.removeIf(i -> i.getProductId().equals(productId));
		touch();
	}

	void clearItems() {
		items.clear();
		touch();
	}

	private Optional<CartItem> findItem(Long productId) {
		return items.stream().filter(i -> i.getProductId().equals(productId)).findFirst();
	}
}
