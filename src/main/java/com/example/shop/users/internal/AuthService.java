package com.example.shop.users.internal;

import java.time.Instant;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AuthService {

	record TokenPair(String accessToken, String refreshToken, long expiresIn) {
	}

	private final UserRepository users;
	private final PasswordEncoder encoder;
	private final TokenService tokens;
	private final RefreshTokenRepository refreshTokens;
	private final JwtProperties props;

	AuthService(
		UserRepository users,
		PasswordEncoder encoder,
		TokenService tokens,
		RefreshTokenRepository refreshTokens,
		JwtProperties props
	) {
		this.users = users;
		this.encoder = encoder;
		this.tokens = tokens;
		this.refreshTokens = refreshTokens;
		this.props = props;
	}

	@Transactional
	TokenPair login(String email, String password) {
		var user = users.findByEmail(email.trim().toLowerCase())
			.filter(u -> encoder.matches(password, u.getPasswordHash()))
			.orElseThrow(() -> new BadCredentialsException("Невірний email або пароль"));
		return pair(user);
	}

	@Transactional
	TokenPair refresh(String rawRefresh) {
		var stored = refreshTokens.findByTokenHash(TokenService.sha256(rawRefresh))
			.filter(t -> t.getExpiresAt().isAfter(Instant.now()))
			.orElseThrow(() -> new BadCredentialsException("Невалідний refresh-токен"));
		refreshTokens.delete(stored);
		var user = users.findById(stored.getUserId()).orElseThrow();
		return pair(user);
	}

	@Transactional
	void logout(String rawRefresh) {
		refreshTokens.deleteByTokenHash(TokenService.sha256(rawRefresh));
	}

	private TokenPair pair(User user) {
		return new TokenPair(tokens.issueAccess(user), tokens.issueRefresh(user), props.accessTtl().toSeconds());
	}
}
