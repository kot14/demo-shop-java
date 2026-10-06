package com.example.shop.catalog;

import java.util.Optional;

public interface CatalogApi {

	Optional<ProductInfo> findActive(Long id);

	boolean reserveStock(Long id, int qty);

	void releaseStock(Long id, int qty);
}
