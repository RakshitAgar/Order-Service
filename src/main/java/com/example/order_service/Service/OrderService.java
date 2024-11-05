package com.example.order_service.Service;

import com.example.order_service.DTO.OrderRequestDTO;
import com.example.order_service.Exceptions.InvalidOrderCredentials;
import com.example.order_service.Exceptions.InvalidOrderItemCredentials;
import com.example.order_service.Exceptions.OrderItemsEmptyException;
import com.example.order_service.Exceptions.OrderNotFoundException;
import com.example.order_service.Model.Order;
import com.example.order_service.Model.OrderItem;
import com.example.order_service.Repository.OrderRepository;
import com.example.order_service.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CatalogServiceClient catalogServiceClient;

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
            List<OrderRequestDTO.OrderItemDTO> orderItemsDTO = orderRequestDTO.getOrderItems();
            if (orderItemsDTO.isEmpty()) {
                throw new OrderItemsEmptyException("Order items are empty");
            }

            // Validate restaurantId
            List<Map<String, Object>> restaurants = catalogServiceClient.getRestaurants();
            boolean isValidRestaurant = restaurants.stream()
                    .anyMatch(restaurant -> restaurant.get("id").equals(orderRequestDTO.getRestaurantId().intValue()));
            if (!isValidRestaurant) {
                throw new InvalidOrderCredentials("Invalid restaurant ID");
            }

            Map<String, Object> restaurant = catalogServiceClient.getRestaurantById(orderRequestDTO.getRestaurantId());
            List<Map<String, Object>> menuItems = (List<Map<String, Object>>) restaurant.get("menuItems");

            List<OrderItem> newOrderItems = orderItemsDTO.stream()
                    .map(itemDTO -> {
                        Map<String, Object> menuItem = menuItems.stream()
                                .filter(mi -> mi.get("id").equals(itemDTO.getMenuItemId().intValue()))
                                .findFirst()
                                .orElseThrow(() -> new InvalidOrderItemCredentials("Invalid menu item ID: " + itemDTO.getMenuItemId()));
                        return new OrderItem(itemDTO.getMenuItemId(), (String) menuItem.get("name"), (Double) menuItem.get("price"), itemDTO.getQuantity());
                    })
                    .collect(Collectors.toList());

            // Create a new Order entity
            Order order = new Order(orderRequestDTO.getRestaurantId(), orderRequestDTO.getCustomerId(), newOrderItems, orderRequestDTO.getDeliveryAddress());

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

    public List<Order> getAllOrder() {
        try {
            List<Order> orders = orderRepository.findAll();
            if (orders.isEmpty()) {
                throw new OrderItemsEmptyException("No orders found");
            }
            return orders;
        } catch (OrderItemsEmptyException e) {
            throw new OrderItemsEmptyException(e.getMessage());
        }
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.setStatus(OrderStatus.valueOf(status));
        orderRepository.save(order);
    }
}
