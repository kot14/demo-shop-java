package com.example.shop.cart.internal;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByCustomerId(Long customerId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from Cart c where c.customerId = :customerId")
	void clearByCustomerId(@Param("customerId") Long customerId);

	@Query("select c from Cart c where c.updatedAt < :cutoff")
	List<Cart> findAbandonedBefore(@Param("cutoff") Instant cutoff);
}
