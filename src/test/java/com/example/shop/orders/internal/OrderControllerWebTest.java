package com.example.shop.orders.internal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrderController.class)
@Import(OrderControllerWebTest.TestSecurityConfig.class)
class OrderControllerWebTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	OrdersService ordersService;

	@Test
	void myOrdersAcceptsJwt() throws Exception {
		when(ordersService.myOrders(eq(42L))).thenReturn(List.of());

		mockMvc.perform(get("/orders/my").with(jwt().jwt(j -> j.subject("42").claim("role", "CUSTOMER"))))
			.andExpect(status().isOk());
	}

	static class TestSecurityConfig {

		@Bean
		JwtDecoder jwtDecoder() {
			return token -> {
				throw new UnsupportedOperationException("Use jwt() post-processor in tests");
			};
		}

		@Bean
		JwtAuthenticationConverter jwtAuthConverter() {
			var authorities = new JwtGrantedAuthoritiesConverter();
			authorities.setAuthoritiesClaimName("role");
			authorities.setAuthorityPrefix("ROLE_");
			var converter = new JwtAuthenticationConverter();
			converter.setJwtGrantedAuthoritiesConverter(authorities);
			return converter;
		}

		@Bean
		SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter conv) throws Exception {
			return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(a -> a.anyRequest().authenticated())
				.oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(conv)))
				.build();
		}
	}
}
