package com.example.shop.cart.internal;

import com.example.shop.orders.OrderPlaced;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
class CartListener {

	private final CartRepository carts;

	CartListener(CartRepository carts) {
		this.carts = carts;
	}

	@ApplicationModuleListener
	void on(OrderPlaced event) {
		carts.clearByCustomerId(event.customerId());
	}
}
