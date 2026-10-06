package com.example.shop;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

	@Test
	void modulesRespectBoundaries() {
		ApplicationModules.of(ShopApplication.class).verify();
	}
}
