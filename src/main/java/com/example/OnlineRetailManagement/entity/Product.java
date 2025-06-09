package com.example.OnlineRetailManagement.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="title")
    private String title;
    @Column(name="title_description")
    private String titleDescription;
    @Column(name="owned_by")
    private Long ownedBy;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="category")
    private String category;
    @Column(name="image_urls")
    private String imageURLs;
    @Column(name="actual_price")
    private Integer actualPrice;
    @Column(name = "discounted_price")
    private Integer discountedPrice;
    @Column(name = "total_quantity")
    private Integer totalQuantity;
    @Column(name = "available_quantity")
    private Integer availableQuantity;
    @Column(name = "Description")
    private String description;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "dimensions")
    private String dimensions;
    @Column(name = "delivery_time")
    private String deliveryTime;
    @Column(name = "rating")
    private Double rating;
    @Column(name = "review")
    private String review;
    @Column(name = "sold_quantity")
    private Integer soldQuantity;
}
