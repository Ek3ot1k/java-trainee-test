package com.foxmarket.ordertest.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Order " + orderId + " not found");
    }
}