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
import java.util.List;


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

    @GetMapping("/cart/{userId}")
    public ResponseEntity<?> addCart(@PathVariable("userId") Long userId){
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<Cart> cartList = cartService.getCartByUserId(userId);
            generalResponse.setCode(HttpStatus.OK.value());
            generalResponse.setMsg("List of items in the cart");
            HashMap<String, List<Cart>> cartItems = new HashMap<>();
            cartItems.put("cart_items", cartList);
            generalResponse.setData(cartItems);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Cannot fetch items for this user");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable("cartId") Long cartId){
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            cartService.deleteCartByUserId(cartId);
            generalResponse.setCode(HttpStatus.OK.value());
            generalResponse.setMsg("Cart Item Deleted");
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Cart Item not deleted");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("cart/{id}")
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable("id") Long cartId, @RequestBody Cart cart) {
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer quantity = cart.getQuantity();
            Cart cartItemModify = cartService.getCartByCartId(cartId);
            cartItemModify.setQuantity(quantity);
            cartService.saveCart(cartItemModify);
            generalResponse.setMsg("Cart item Quantity set to "+ quantity);
            generalResponse.setCode(HttpStatus.OK.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

}
