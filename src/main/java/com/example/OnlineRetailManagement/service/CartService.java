package com.example.OnlineRetailManagement.service;

import com.example.OnlineRetailManagement.entity.Cart;
import com.example.OnlineRetailManagement.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public List<Cart> getCartByUserId(Long userid){
        return cartRepository.getCartByUserId(userid);
    }

}
