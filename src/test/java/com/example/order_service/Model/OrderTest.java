package com.example.order_service.Model;

import com.example.order_service.Exceptions.InvalidOrderCredentials;
import com.example.order_service.Exceptions.OrderItemsEmptyException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    public void testOrderCreation(){
        assertDoesNotThrow(() -> {
            new Order(1L,1L, List.of(new OrderItem(1L, "Item1", 2, 10)),"Address");
        });
    }

    @Test
    public void testOrder_RestaurantIdNull() {
        assertThrows(InvalidOrderCredentials.class , () -> {
            new Order(null,1L, List.of(new OrderItem(1L, "Item1", 2, 10)),"Address");
        });
    }

    @Test
    public void testOrder_CustomerIdNull() {
        assertThrows(InvalidOrderCredentials.class , () -> {
            new Order(1L,null, List.of(new OrderItem(1L, "Item1", 2, 10)),"Address");
        });
    }

    @Test
    public void testOrder_OrderItemNull() {
        assertThrows(OrderItemsEmptyException.class , () -> {
            new Order(1L,1L, null,"Address");
        });
    }

    @Test
    public void testOrder_OrderItemEmpty() {
        assertThrows(OrderItemsEmptyException.class , () -> {
            new Order(1L,1L, new ArrayList<>(),"Address");
        });
    }


}