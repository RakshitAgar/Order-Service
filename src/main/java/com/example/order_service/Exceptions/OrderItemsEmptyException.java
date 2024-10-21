package com.example.order_service.Exceptions;

public class OrderItemsEmptyException extends RuntimeException {
    public OrderItemsEmptyException(String message) {
        super(message);
    }
}
