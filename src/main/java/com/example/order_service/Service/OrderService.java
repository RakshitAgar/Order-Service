package com.example.order_service.Service;

import com.example.order_service.DTO.OrderRequestDTO;
import com.example.order_service.Exceptions.InvalidOrderCredentials;
import com.example.order_service.Exceptions.InvalidOrderItemCredentials;
import com.example.order_service.Exceptions.OrderItemsEmptyException;
import com.example.order_service.Exceptions.OrderNotFoundException;
import com.example.order_service.Model.Order;
import com.example.order_service.Model.OrderItem;
import com.example.order_service.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order getOrder(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
            return order;
        } catch (OrderNotFoundException e) {
            throw new OrderNotFoundException(e.getMessage());
        }
    }

    public void addOrder(OrderRequestDTO orderRequestDTO) {
        try {
            List<OrderItem> orderItems = orderRequestDTO.getOrderItems();
            if (orderItems.isEmpty()) {
                throw new OrderItemsEmptyException("Order items are empty");
            }
            List<OrderItem> newOrderItems = orderItems.stream()
                    .map(item -> new OrderItem(item.getMenuItemId(), item.getMenuItemName(), item.getPrice(), item.getQuantity()))
                    .collect(Collectors.toList());

            // Create a new Order entity
            Order order = new Order(orderRequestDTO.getRestaurantId(), orderRequestDTO.getCustomerId(), newOrderItems);

            // Set the order reference in each OrderItem
            newOrderItems.forEach(item -> item.setOrder(order));

            // Save the order
            orderRepository.save(order);

        } catch (OrderItemsEmptyException e) {
            throw new OrderItemsEmptyException(e.getMessage());
        } catch (InvalidOrderCredentials e) {
            throw new InvalidOrderCredentials(e.getMessage());
        } catch (InvalidOrderItemCredentials e) {
            throw new InvalidOrderItemCredentials(e.getMessage());
        }
    }
}
