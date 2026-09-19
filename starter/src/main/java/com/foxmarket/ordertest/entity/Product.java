package com.foxmarket.ordertest.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean available;

    protected Product() {
    }

    public Product(String name, BigDecimal price, Integer stock, Boolean available) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void decreaseStock(int quantity) {
        this.stock -= quantity;
    }
}