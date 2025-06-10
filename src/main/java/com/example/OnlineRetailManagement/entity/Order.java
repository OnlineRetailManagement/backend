package com.example.OnlineRetailManagement.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ required for MySQL AUTO_INCREMENT
    @Column(name = "id")
    private Long id; // maps to BIGINT PRIMARY KEY

    @Column(name = "cart_id", nullable = false)
    private Long cartId; // INT

    @Column(name = "user_id", nullable = false)
    private Long userId; // BIGINT

    @Column(name = "product_id", nullable = false)
    private Long productId; // BIGINT

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // INT

    @Column(name = "checkout_date", nullable = false)
    private LocalDateTime checkoutDate; // DATETIME

    @Column(name = "order_status", nullable = false)
    private String orderStatus; // VARCHAR(255)

    @Column(name = "payment_id", nullable = false)
    private Long paymentId; // VARCHAR(255)

    @Column(name = "address_id", nullable = false)
    private Long addressId; // VARCHAR(255)

}
