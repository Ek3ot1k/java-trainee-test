package com.foxmarket.ordertest.exception;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(Long productId) {
        super("Product " + productId + " is duplicated in request");
    }
}