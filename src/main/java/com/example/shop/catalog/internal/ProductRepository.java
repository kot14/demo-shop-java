package com.example.shop.catalog.internal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByActiveTrue();

	boolean existsBySku(String sku);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Product p set p.stock = p.stock - :qty where p.id = :id and p.stock >= :qty")
	int tryReserve(@Param("id") Long id, @Param("qty") int qty);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Product p set p.stock = p.stock + :qty where p.id = :id")
	int release(@Param("id") Long id, @Param("qty") int qty);
}
