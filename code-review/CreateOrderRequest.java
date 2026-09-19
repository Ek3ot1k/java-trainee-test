package com.foxmarket.review;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(List<Item> items) {

    public record Item(Long productId, int quantity, BigDecimal unitPrice) {
    }
}
