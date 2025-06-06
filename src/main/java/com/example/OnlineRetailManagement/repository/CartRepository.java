package com.example.OnlineRetailManagement.repository;

import com.example.OnlineRetailManagement.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
