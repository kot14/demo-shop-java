package com.example.shop.users.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
class TokenService {

	private final JwtEncoder encoder;
	private final JwtProperties props;
	private final RefreshTokenRepository refreshTokens;
	private final SecureRandom random = new SecureRandom();

	TokenService(JwtEncoder encoder, JwtProperties props, RefreshTokenRepository refreshTokens) {
		this.encoder = encoder;
		this.props = props;
		this.refreshTokens = refreshTokens;
	}

	String issueAccess(User user) {
		var now = Instant.now();
		var claims = JwtClaimsSet.builder()
			.issuer("shop")
			.subject(user.getId().toString())
			.issuedAt(now)
			.expiresAt(now.plus(props.accessTtl()))
			.claim("role", user.getRole().name())
			.build();
		var header = JwsHeader.with(MacAlgorithm.HS256).build();
		return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	String issueRefresh(User user) {
		var bytes = new byte[32];
		random.nextBytes(bytes);
		var raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		refreshTokens.save(new RefreshToken(user.getId(), sha256(raw), Instant.now().plus(props.refreshTtl())));
		return raw;
	}

	static String sha256(String s) {
		try {
			var digest = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
