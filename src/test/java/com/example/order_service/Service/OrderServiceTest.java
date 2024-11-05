package com.example.order_service.Service;

import com.example.order_service.DTO.OrderRequestDTO;
import com.example.order_service.Exceptions.InvalidOrderCredentials;
import com.example.order_service.Exceptions.InvalidOrderItemCredentials;
import com.example.order_service.Exceptions.OrderItemsEmptyException;
import com.example.order_service.Exceptions.OrderNotFoundException;
import com.example.order_service.Model.Order;
import com.example.order_service.Model.OrderItem;
import com.example.order_service.Repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CatalogServiceClient catalogServiceClient;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrder_Success() {
        Long orderId = 1L;
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder(orderId);

        assertNotNull(result);
        assertEquals(order, result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testGetOrder_OrderNotFoundException() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId));
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testAddOrder_Success() throws Exception {
        OrderRequestDTO.OrderItemDTO orderItemDTO = new OrderRequestDTO.OrderItemDTO();
        setField(orderItemDTO, "menuItemId", 1L);
        setField(orderItemDTO, "quantity", 2);

        List<OrderRequestDTO.OrderItemDTO> orderItemsDTO = Arrays.asList(orderItemDTO);
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        setField(orderRequestDTO, "restaurantId", 1L);
        setField(orderRequestDTO, "customerId", 1L);
        setField(orderRequestDTO, "orderItems", orderItemsDTO);
        setField(orderRequestDTO, "deliveryAddress", "123 Main St");


        // Mock the catalogServiceClient to return a valid list of restaurants
        Map<String, Object> restaurant = Map.of("id", 1);
        List<Map<String, Object>> restaurants = Arrays.asList(restaurant);
        when(catalogServiceClient.getRestaurants()).thenReturn(restaurants);

        // Mock the catalogServiceClient to return a valid restaurant with menu items
        Map<String, Object> menuItem = Map.of("id", 1, "name", "Item1", "price", 10.0);
        Map<String, Object> restaurantDetails = Map.of("menuItems", Arrays.asList(menuItem));
        when(catalogServiceClient.getRestaurantById(1L)).thenReturn(restaurantDetails);

        orderService.addOrder(orderRequestDTO);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testAddOrder_OrderItemsEmptyException() throws Exception {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        setField(orderRequestDTO, "restaurantId", 1L);
        setField(orderRequestDTO, "customerId", 1L);
        setField(orderRequestDTO, "orderItems", Arrays.asList());

        assertThrows(OrderItemsEmptyException.class, () -> orderService.addOrder(orderRequestDTO));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testAddOrder_InvalidOrderItemCredentials() throws Exception {
        // Create an OrderItem with invalid credentials (empty name)
        OrderItem orderItem = new OrderItem();
        setField(orderItem, "menuItemId", 1L);
        setField(orderItem, "menuItemName", ""); // Invalid name
        setField(orderItem, "price", 10.0);
        setField(orderItem, "quantity", 2);

        List<OrderItem> orderItems = Arrays.asList(orderItem);
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        setField(orderRequestDTO, "restaurantId", 1L);
        setField(orderRequestDTO, "customerId", 1L);
        setField(orderRequestDTO, "orderItems", orderItems);

        // The exception should be thrown during the execution of addOrder
        assertThrows(InvalidOrderCredentials.class, () -> orderService.addOrder(orderRequestDTO));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetAllOrderSuccess() {
        List<Order> orders = List.of(new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrder();
        assertEquals(orders, result);
    }

    @Test
    void testGetAllOrderNotFound() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(OrderItemsEmptyException.class, () -> {
            orderService.getAllOrder();
        });
    }
}