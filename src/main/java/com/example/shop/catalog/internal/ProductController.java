package com.example.shop.catalog.internal;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
class ProductController {

	private final CatalogService catalog;

	ProductController(CatalogService catalog) {
		this.catalog = catalog;
	}

	record CreateProductDto(
		@NotBlank @Size(max = 64) String sku,
		@NotBlank @Size(max = 255) String name,
		String description,
		@NotNull @Min(0) Long priceMinor,
		@NotNull @Min(0) Integer stock
	) {
	}

	record UpdateProductDto(
		@NotBlank @Size(max = 255) String name,
		String description,
		@NotNull @Min(0) Long priceMinor,
		@NotNull @Min(0) Integer stock,
		@NotNull Boolean active
	) {
	}

	@GetMapping
	List<CatalogService.ProductDto> list() {
		return catalog.listActive();
	}

	@GetMapping("/{id}")
	CatalogService.ProductDto get(@PathVariable Long id) {
		return catalog.get(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	CatalogService.ProductDto create(@Valid @RequestBody CreateProductDto dto) {
		var info = catalog.create(new CatalogService.CreateProductRequest(
			dto.sku(), dto.name(), dto.description(), dto.priceMinor(), dto.stock()
		));
		return catalog.get(info.id());
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	CatalogService.ProductDto update(@PathVariable Long id, @Valid @RequestBody UpdateProductDto dto) {
		catalog.update(id, new CatalogService.UpdateProductRequest(
			dto.name(), dto.description(), dto.priceMinor(), dto.stock(), dto.active()
		));
		return catalog.get(id);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void delete(@PathVariable Long id) {
		catalog.delete(id);
	}
}
