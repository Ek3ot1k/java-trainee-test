package com.foxmarket.ordertest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull
        @NotEmpty
        List<@NotNull @Valid CreateOrderItemRequest> items
) {
}