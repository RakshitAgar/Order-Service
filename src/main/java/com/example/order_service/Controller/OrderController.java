package com.example.order_service.Controller;


import com.example.order_service.DTO.OrderRequestDTO;
import com.example.order_service.Exceptions.InvalidOrderCredentials;
import com.example.order_service.Exceptions.InvalidOrderItemCredentials;
import com.example.order_service.Exceptions.OrderItemsEmptyException;
import com.example.order_service.Exceptions.OrderNotFoundException;
import com.example.order_service.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            return ResponseEntity.ok(orderService.getAllOrder());
        } catch (OrderItemsEmptyException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // GET /orders
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrders(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(orderService.getOrder(orderId));
        } catch (OrderNotFoundException | OrderItemsEmptyException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // POST /orders
    @PostMapping
    public ResponseEntity<?> addOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        try {
            orderService.addOrder(orderRequestDTO);
            return ResponseEntity.ok("Order added successfully");
        } catch (OrderItemsEmptyException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidOrderCredentials | InvalidOrderItemCredentials e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> statusUpdate) {
        try {
            String status = statusUpdate.get("status");
            orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok("Order status updated successfully");
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}


/*
    * API ENDPOINTS
    * GET /orders
    * POST /orders
    * GET /orders/{orderId}
 */
