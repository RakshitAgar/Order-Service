package com.example.order_service.Exceptions;

public class InvalidOrderCredentials extends RuntimeException {
    public InvalidOrderCredentials(String message) {
        super(message);
    }
}
