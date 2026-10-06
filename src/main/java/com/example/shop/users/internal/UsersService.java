package com.example.shop.users.internal;

import com.example.shop.shared.ConflictException;
import com.example.shop.users.UsersApi;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UsersService implements UsersApi {

	private final UserRepository users;
	private final PasswordEncoder encoder;

	UsersService(UserRepository users, PasswordEncoder encoder) {
		this.users = users;
		this.encoder = encoder;
	}

	@Transactional
	Long register(String email, String rawPassword, String fullName) {
		var normalized = email.trim().toLowerCase();
		if (users.existsByEmail(normalized)) {
			throw new ConflictException("Email вже зайнятий");
		}
		return users.save(new User(normalized, encoder.encode(rawPassword), fullName)).getId();
	}

	@Override
	public boolean exists(Long userId) {
		return users.existsById(userId);
	}
}
