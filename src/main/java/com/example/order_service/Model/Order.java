package com.example.order_service.Model;


import com.example.order_service.Exceptions.InvalidOrderCredentials;
import com.example.order_service.Exceptions.OrderItemsEmptyException;
import com.example.order_service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Entity
@Getter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long restaurantId;
    private Long customerId;
    private Double totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public Order(Long restaurantId, Long customerId, List<OrderItem> orderItems) {
        if(restaurantId == null || customerId == null) {
            throw new InvalidOrderCredentials("Restaurant id and customer id cannot be null");
        }
        if(orderItems == null || orderItems.isEmpty()) {
            throw new OrderItemsEmptyException("Order items cannot be empty");
        }
        this.restaurantId = restaurantId;
        this.customerId = customerId;
        this.totalPrice = calculateTotalPrice(orderItems);
        this.status = OrderStatus.CREATED;
        this.orderItems = orderItems;
    }

    private Double calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream().mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity()).sum();
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public Order() {
    }

}
