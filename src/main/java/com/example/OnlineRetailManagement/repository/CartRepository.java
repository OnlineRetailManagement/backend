package com.example.OnlineRetailManagement.repository;

import com.example.OnlineRetailManagement.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query(value = "SELECT * FROM cart WHERE userid = :userid", nativeQuery = true)
    List<Cart> getCartByUserId(@Param("userid") long userid);
}
