package com.example.shop.cart.internal;

import java.time.Instant;
import java.util.List;

import com.example.shop.catalog.CatalogApi;
import com.example.shop.shared.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CartService {

	private final CartRepository carts;
	private final CatalogApi catalog;

	CartService(CartRepository carts, CatalogApi catalog) {
		this.carts = carts;
		this.catalog = catalog;
	}

	@Transactional(readOnly = true)
	CartView view(Long customerId) {
		return carts.findByCustomerId(customerId)
			.map(this::toView)
			.orElseGet(() -> new CartView(customerId, List.of()));
	}

	@Transactional
	CartView add(Long customerId, Long productId, int quantity) {
		catalog.findActive(productId).orElseThrow(() -> new NotFoundException("Товар не знайдено"));
		var cart = carts.findByCustomerId(customerId).orElseGet(() -> carts.save(new Cart(customerId)));
		var existingQty = cart.getItems().stream()
			.filter(i -> i.getProductId().equals(productId))
			.mapToInt(CartItem::getQuantity)
			.findFirst()
			.orElse(0);
		cart.upsertItem(productId, existingQty + quantity);
		return toView(cart);
	}

	@Transactional
	CartView setQuantity(Long customerId, Long productId, int quantity) {
		catalog.findActive(productId).orElseThrow(() -> new NotFoundException("Товар не знайдено"));
		var cart = carts.findByCustomerId(customerId).orElseThrow(() -> new NotFoundException("Кошик порожній"));
		if (quantity <= 0) {
			cart.removeItem(productId);
		}
		else {
			cart.upsertItem(productId, quantity);
		}
		return toView(cart);
	}

	@Transactional
	CartView remove(Long customerId, Long productId) {
		var cart = carts.findByCustomerId(customerId).orElseThrow(() -> new NotFoundException("Кошик порожній"));
		cart.removeItem(productId);
		return toView(cart);
	}

	@Transactional
	void purgeAbandoned(Instant cutoff) {
		var abandoned = carts.findAbandonedBefore(cutoff);
		if (!abandoned.isEmpty()) {
			carts.deleteAll(abandoned);
		}
	}

	private CartView toView(Cart cart) {
		var lines = cart.getItems().stream()
			.map(i -> new CartLine(i.getProductId(), i.getQuantity()))
			.toList();
		return new CartView(cart.getCustomerId(), lines);
	}

	record CartLine(Long productId, int quantity) {
	}

	record CartView(Long customerId, List<CartLine> items) {
	}
}
