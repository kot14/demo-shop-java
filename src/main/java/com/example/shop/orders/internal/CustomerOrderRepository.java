package com.example.shop.orders.internal;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

	List<CustomerOrder> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

	Optional<CustomerOrder> findByIdAndCustomerId(Long id, Long customerId);

	List<CustomerOrder> findByStatusAndCreatedAtBefore(OrderStatus status, Instant createdAt);

	@Query("""
		select coalesce(sum(o.totalMinor), 0)
		from CustomerOrder o
		where o.status = com.example.shop.orders.internal.OrderStatus.PAID
		  and o.paidAt >= :from
		  and o.paidAt < :to
		""")
	long sumPaidBetween(@Param("from") Instant from, @Param("to") Instant to);
}
