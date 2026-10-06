package com.example.shop.cart.internal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
class CartController {

	private final CartService carts;

	CartController(CartService carts) {
		this.carts = carts;
	}

	record UpsertItemRequest(@NotNull Long productId, @NotNull @Min(1) Integer quantity) {
	}

	record SetQuantityRequest(@NotNull @Min(0) Integer quantity) {
	}

	@GetMapping
	CartService.CartView view(@AuthenticationPrincipal Jwt jwt) {
		return carts.view(customerId(jwt));
	}

	@PostMapping("/items")
	@ResponseStatus(HttpStatus.CREATED)
	CartService.CartView add(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UpsertItemRequest request) {
		return carts.add(customerId(jwt), request.productId(), request.quantity());
	}

	@PutMapping("/items/{productId}")
	CartService.CartView setQuantity(
		@AuthenticationPrincipal Jwt jwt,
		@PathVariable Long productId,
		@Valid @RequestBody SetQuantityRequest request
	) {
		return carts.setQuantity(customerId(jwt), productId, request.quantity());
	}

	@DeleteMapping("/items/{productId}")
	CartService.CartView remove(@AuthenticationPrincipal Jwt jwt, @PathVariable Long productId) {
		return carts.remove(customerId(jwt), productId);
	}

	private static Long customerId(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}
}
