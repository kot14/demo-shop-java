package com.example.shop.users.internal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
class AuthController {

	private final AuthService auth;
	private final UsersService usersService;

	AuthController(AuthService auth, UsersService usersService) {
		this.auth = auth;
		this.usersService = usersService;
	}

	record RegisterRequest(
		@Email @NotBlank String email,
		@Size(min = 10, max = 64) String password,
		@NotBlank String fullName
	) {
	}

	record LoginRequest(@NotBlank String email, @NotBlank String password) {
	}

	record RefreshRequest(@NotBlank String refreshToken) {
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	void register(@Valid @RequestBody RegisterRequest request) {
		usersService.register(request.email(), request.password(), request.fullName());
	}

	@PostMapping("/login")
	AuthService.TokenPair login(@Valid @RequestBody LoginRequest request) {
		return auth.login(request.email(), request.password());
	}

	@PostMapping("/refresh")
	AuthService.TokenPair refresh(@Valid @RequestBody RefreshRequest request) {
		return auth.refresh(request.refreshToken());
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void logout(@Valid @RequestBody RefreshRequest request) {
		auth.logout(request.refreshToken());
	}
}
