package com.foxmarket.ordertest.repository;

import com.foxmarket.ordertest.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}