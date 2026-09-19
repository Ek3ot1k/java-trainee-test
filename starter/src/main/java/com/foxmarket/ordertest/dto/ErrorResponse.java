package com.foxmarket.ordertest.dto;

public record ErrorResponse(
        String code,
        String message
) {
}