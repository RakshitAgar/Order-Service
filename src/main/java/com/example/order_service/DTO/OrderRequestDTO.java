package com.example.order_service.DTO;

import com.example.order_service.Model.OrderItem;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequestDTO {
    private List<OrderItem> orderItems;
    private Long restaurantId;
    private Long customerId;

    public OrderRequestDTO() {

    }
}
