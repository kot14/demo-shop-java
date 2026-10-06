package com.example.shop.users.internal;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.jwt")
record JwtProperties(String secret, Duration accessTtl, Duration refreshTtl) {
}
