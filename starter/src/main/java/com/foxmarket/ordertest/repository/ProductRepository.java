package com.foxmarket.ordertest.repository;

import com.foxmarket.ordertest.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}