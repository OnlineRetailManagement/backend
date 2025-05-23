package com.example.OnlineRetailManagement.service;

import com.example.OnlineRetailManagement.repository.ProductRepository;
import com.example.OnlineRetailManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ProductService{
    @Autowired
    private ProductRepository productRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
}
