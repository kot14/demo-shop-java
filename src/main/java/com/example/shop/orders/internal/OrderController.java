package com.example.shop.orders.internal;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
class OrderController {

	private final OrdersService orders;

	OrderController(OrdersService orders) {
		this.orders = orders;
	}

	record PlaceOrderLine(@NotNull Long productId, @NotNull @Min(1) Integer quantity) {
	}

	record PlaceOrderRequest(@NotEmpty List<@Valid PlaceOrderLine> lines) {
	}

	record PlaceOrderResponse(Long orderId) {
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	PlaceOrderResponse place(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody PlaceOrderRequest request) {
		var lines = request.lines().stream()
			.map(l -> new OrdersService.Line(l.productId(), l.quantity()))
			.toList();
		return new PlaceOrderResponse(orders.place(customerId(jwt), lines));
	}

	@GetMapping("/my")
	List<OrdersService.OrderView> my(@AuthenticationPrincipal Jwt jwt) {
		return orders.myOrders(customerId(jwt));
	}

	@GetMapping("/{id}")
	OrdersService.OrderView one(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
		return orders.getMine(customerId(jwt), id);
	}

	private static Long customerId(Jwt jwt) {
		return Long.valueOf(jwt.getSubject());
	}
}
