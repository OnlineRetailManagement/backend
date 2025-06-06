package com.example.OnlineRetailManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartid;

    @Column(nullable = false)
    private Long userid;

    @Column(nullable = false)
    private Long productid;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "createdat", nullable = false, updatable = false)
    private LocalDateTime createdat;

    @PrePersist
    protected void onCreate() {
        this.createdat = LocalDateTime.now();
    }
}
