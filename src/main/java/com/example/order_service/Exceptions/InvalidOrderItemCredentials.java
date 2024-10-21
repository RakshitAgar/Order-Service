package com.example.order_service.Exceptions;

public class InvalidOrderItemCredentials extends RuntimeException {
    public InvalidOrderItemCredentials(String message) {
        super(message);
    }
}
