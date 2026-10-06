package com.example.shop.users.internal;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

class LoginRateLimitFilter extends OncePerRequestFilter {

	private static final int LIMIT = 20;
	private static final long WINDOW_SECONDS = 60;

	private final Map<String, Window> windows = new ConcurrentHashMap<>();

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		if ("POST".equalsIgnoreCase(request.getMethod()) && "/auth/login".equals(request.getRequestURI())) {
			var key = request.getRemoteAddr();
			var now = Instant.now().getEpochSecond();
			var window = windows.compute(key, (k, current) -> {
				if (current == null || now - current.startEpochSec >= WINDOW_SECONDS) {
					return new Window(now, new AtomicInteger(1));
				}
				current.count.incrementAndGet();
				return current;
			});
			if (window.count.get() > LIMIT) {
				response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
				response.getWriter().write("Too many login attempts");
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	private record Window(long startEpochSec, AtomicInteger count) {
	}
}
