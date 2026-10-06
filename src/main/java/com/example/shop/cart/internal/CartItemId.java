package com.example.shop.cart.internal;

import java.io.Serializable;
import java.util.Objects;

class CartItemId implements Serializable {

	private Long cart;
	private Long productId;

	protected CartItemId() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CartItemId that)) {
			return false;
		}
		return Objects.equals(cart, that.cart) && Objects.equals(productId, that.productId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cart, productId);
	}
}
