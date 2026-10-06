package com.example.shop.shared;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

	private static final String BEARER_SCHEME = "bearer-jwt";

	@Bean
	OpenAPI shopOpenApi() {
		return new OpenAPI()
			.info(new Info()
				.title("Shop API")
				.version("0.0.1")
				.description("""
					REST API магазину.
					1. POST /auth/login → скопіюй accessToken
					2. Натисни Authorize → встав токен (без Bearer)
					3. Викликай захищені ендпоінти
					"""))
			.components(new Components().addSecuritySchemes(BEARER_SCHEME,
				new SecurityScheme()
					.name(BEARER_SCHEME)
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")))
			.addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
	}
}
