package com.example.order_service.Model;

import com.example.order_service.Exceptions.InvalidOrderItemCredentials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    public void testOrderItem_Success() {
        assertDoesNotThrow(() -> {
            new OrderItem(1L,"Burger",10.0 ,2);
        });
    }

    @Test
    public void testOrderItem_MenuIdNull() {
        assertThrows(InvalidOrderItemCredentials.class, () -> {
            new OrderItem(null,"Burger",10.0 ,2);
        });
    }

    @Test
    public void testOrderItem_MenuItemNameEmpty() {
        assertThrows(InvalidOrderItemCredentials.class, () -> {
            new OrderItem(1L,"",10.0 ,2);
        });
    }

    @Test
    public void testOrderItem_PriceZero() {
        assertThrows(InvalidOrderItemCredentials.class, () -> {
            new OrderItem(1L,"",0 ,2);
        });
    }

    @Test
    public void testOrderItem_QuantityZero() {
        assertThrows(InvalidOrderItemCredentials.class, () -> {
            new OrderItem(1L,"",0 ,0);
        });
    }

}