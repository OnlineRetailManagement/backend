package com.example.OnlineRetailManagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String titleDescription;
    private String ownedBy;
    private Date createdAt;
    private String category;
    private String imageURLs;
    private Integer actualPrice;
    private Integer discountedPrice;
    private Integer totalQuantity;
    private String availableQuantity;
    private String Description;
    private Double weight;
    private String dimensions;
    private Date deliveryTime;
    private Double rating;
    private String review;
    private Integer soldQuantity;
}
