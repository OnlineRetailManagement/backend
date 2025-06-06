package com.example.OnlineRetailManagement.controller;

import com.example.OnlineRetailManagement.entity.Cart;
import com.example.OnlineRetailManagement.entity.GeneralResponse;
import com.example.OnlineRetailManagement.entity.User;
import com.example.OnlineRetailManagement.repository.UserRepository;
import com.example.OnlineRetailManagement.service.CartService;
import com.example.OnlineRetailManagement.service.UserDetailsServiceImpl;
import com.example.OnlineRetailManagement.service.UserService;
import com.example.OnlineRetailManagement.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @GetMapping("/health-check")
    public String healthCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Ok";
    }

    @PostMapping("/cart")
    public ResponseEntity<?> addCart(@RequestBody Cart cart ){
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Cart cartAdded = cartService.saveCart(cart);
            generalResponse.setCode(HttpStatus.OK.value());
            generalResponse.setMsg("Item added to the cart");
            HashMap<String, Long> cartId = new HashMap<>();
            cartId.put("cart_id", cartAdded.getCartid());
            generalResponse.setData(cartId);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Item was not added to the cart");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

}
