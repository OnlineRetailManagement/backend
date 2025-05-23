package com.example.OnlineRetailManagement.repository;

import com.example.OnlineRetailManagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>  {

}
