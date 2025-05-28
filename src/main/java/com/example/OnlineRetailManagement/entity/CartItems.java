package com.example.OnlineRetailManagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "cart_items")
public class CartItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cart_id")
    private String cartId;

    @Column(name = "product_id")
    private Long productId;

    private String quantity;

    @Column(name = "product_price")
    private String productPrice;
}
