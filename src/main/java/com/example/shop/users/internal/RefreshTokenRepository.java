package com.example.shop.users.internal;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	void deleteByTokenHash(String tokenHash);

	@Modifying
	void deleteByExpiresAtBefore(Instant instant);
}
