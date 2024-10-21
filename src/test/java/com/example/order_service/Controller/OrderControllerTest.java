package com.example.order_service.Controller;

import com.example.order_service.DTO.OrderRequestDTO;
import com.example.order_service.Exceptions.InvalidOrderCredentials;
import com.example.order_service.Exceptions.InvalidOrderItemCredentials;
import com.example.order_service.Exceptions.OrderItemsEmptyException;
import com.example.order_service.Exceptions.OrderNotFoundException;
import com.example.order_service.Model.Order;
import com.example.order_service.Service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrder() throws Exception {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        when(orderService.getOrder(orderId)).thenReturn(order);

        mockMvc.perform(get("/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(order)));
    }

    @Test
    void testGetOrderNotFound() throws Exception {
        Long orderId = 1L;

        when(orderService.getOrder(orderId)).thenThrow(new OrderNotFoundException("Order not found: " + orderId));

        mockMvc.perform(get("/orders/{orderId}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order not found: " + orderId));
    }

    @Test
    void testAddOrder() throws Exception {
        String orderRequestJson = """
    {
        "orderItems": [
            {
                "menuItemId": 1,
                "menuItemName": "Item1",
                "price": 10.0,
                "quantity": 1
            }
        ]
    }
    """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Order added successfully"));
    }

    @Test
    void testAddOrderItemsEmpty() throws Exception {
        String orderRequestJson = """
    {
        "restaurantId": 1,
        "customerId": 2,
        "orderItems": []
    }
    """;

        doThrow(new OrderItemsEmptyException("Order items are empty"))
                .when(orderService).addOrder(any(OrderRequestDTO.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Order items are empty"));
    }

    @Test
    void testAddOrderInvalidOrderCredentials() throws Exception {
        String orderRequestJson = """
    {
        "orderItems": [
            {
                "menuItemId": 1,
                "menuItemName": "Item1",
                "price": 10.0,
                "quantity": 1
            }
        ]
    }
    """;

        doThrow(new InvalidOrderCredentials("Invalid order credentials"))
                .when(orderService).addOrder(any(OrderRequestDTO.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid order credentials"));
    }

    @Test
    void testAddOrderInvalidOrderItemCredentials() throws Exception {
        String orderRequestJson = """
    {
        "restaurantId": 1,
        "customerId": 2,
        "orderItems": [
            {
                "menuItemId": 1,
                "menuItemName": "Item1",
                "quantity": 1
            }
        ]
    }
    """;

        doThrow(new InvalidOrderItemCredentials("Invalid order item credentials"))
                .when(orderService).addOrder(any(OrderRequestDTO.class));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid order item credentials"));
    }
}