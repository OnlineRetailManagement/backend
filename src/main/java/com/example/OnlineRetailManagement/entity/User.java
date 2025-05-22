package com.example.OnlineRetailManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String location;
    private String paymentInfo;

    @Column(nullable = false)
    private String role;

    private String firstName;
    private String lastName;
}
