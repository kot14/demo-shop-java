package com.example.shop.catalog.internal;

import java.util.List;
import java.util.Optional;

import com.example.shop.catalog.CatalogApi;
import com.example.shop.catalog.ProductInfo;
import com.example.shop.shared.ConflictException;
import com.example.shop.shared.NotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CatalogService implements CatalogApi {

	private final ProductRepository products;

	CatalogService(ProductRepository products) {
		this.products = products;
	}

	@Override
	@Cacheable(value = "products", key = "#id")
	public Optional<ProductInfo> findActive(Long id) {
		return products.findById(id)
			.filter(Product::isActive)
			.map(p -> new ProductInfo(p.getId(), p.getName(), p.getPriceMinor()));
	}

	@Override
	@Transactional
	public boolean reserveStock(Long id, int qty) {
		return products.tryReserve(id, qty) == 1;
	}

	@Override
	@Transactional
	public void releaseStock(Long id, int qty) {
		products.release(id, qty);
	}

	@Transactional(readOnly = true)
	List<ProductDto> listActive() {
		return products.findByActiveTrue().stream().map(ProductDto::from).toList();
	}

	@Transactional(readOnly = true)
	ProductDto get(Long id) {
		return products.findById(id)
			.filter(Product::isActive)
			.map(ProductDto::from)
			.orElseThrow(() -> new NotFoundException("Товар не знайдено"));
	}

	@Transactional
	@CacheEvict(value = "products", key = "#result.id()")
	ProductInfo create(CreateProductRequest request) {
		if (products.existsBySku(request.sku())) {
			throw new ConflictException("SKU вже зайнятий");
		}
		var saved = products.save(new Product(
			request.sku(),
			request.name(),
			request.description(),
			request.priceMinor(),
			request.stock()
		));
		return new ProductInfo(saved.getId(), saved.getName(), saved.getPriceMinor());
	}

	@Transactional
	@CacheEvict(value = "products", key = "#id")
	ProductInfo update(Long id, UpdateProductRequest request) {
		var product = products.findById(id).orElseThrow(() -> new NotFoundException("Товар не знайдено"));
		product.update(request.name(), request.description(), request.priceMinor(), request.stock(), request.active());
		return new ProductInfo(product.getId(), product.getName(), product.getPriceMinor());
	}

	@Transactional
	@CacheEvict(value = "products", key = "#id")
	void delete(Long id) {
		var product = products.findById(id).orElseThrow(() -> new NotFoundException("Товар не знайдено"));
		product.deactivate();
	}

	record ProductDto(Long id, String sku, String name, String description, long priceMinor, int stock, boolean active) {
		static ProductDto from(Product p) {
			return new ProductDto(p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getPriceMinor(), p.getStock(), p.isActive());
		}
	}

	record CreateProductRequest(String sku, String name, String description, long priceMinor, int stock) {
	}

	record UpdateProductRequest(String name, String description, long priceMinor, int stock, boolean active) {
	}
}
