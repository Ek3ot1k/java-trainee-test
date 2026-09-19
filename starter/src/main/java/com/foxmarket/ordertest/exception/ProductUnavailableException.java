package com.foxmarket.ordertest.exception;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(Long productId) {
        super("Product " + productId + " is unavailable");
    }
}