package com.example.order_service.Model;


import com.example.order_service.Exceptions.InvalidOrderItemCredentials;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    @JsonIgnore
    @JoinColumn(name = "order_id")
    private Order order;

    private Long menuItemId;
    private String menuItemName;
    private double price;
    private int quantity;

    public OrderItem(Long menuItemId, String menuItemName, double price, int quantity) {
        if(menuItemName.isBlank() || menuItemName == null || price < 0 || quantity < 0 || menuItemId == null) {
            throw new InvalidOrderItemCredentials("Invalid order item credentials");
        }
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.price = price;
        this.quantity = quantity;
    }

    public OrderItem() {

    }
}
