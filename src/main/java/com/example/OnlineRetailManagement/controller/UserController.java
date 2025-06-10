package com.example.OnlineRetailManagement.controller;

import com.example.OnlineRetailManagement.DTO.CartResponseDTO;
import com.example.OnlineRetailManagement.DTO.OrderRequestDTO;
import com.example.OnlineRetailManagement.DTO.OrderResponseDTO;
import com.example.OnlineRetailManagement.entity.Cart;
import com.example.OnlineRetailManagement.entity.GeneralResponse;
import com.example.OnlineRetailManagement.DTO.ProductResponseDTO;
import com.example.OnlineRetailManagement.entity.Attachment;
import com.example.OnlineRetailManagement.entity.GeneralResponse;
import com.example.OnlineRetailManagement.entity.Product;
import com.example.OnlineRetailManagement.entity.User;
import com.example.OnlineRetailManagement.repository.UserRepository;
import com.example.OnlineRetailManagement.service.*;
import com.example.OnlineRetailManagement.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AttachmentService attachmentService;

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
            cartId.put("cart_id", cartAdded.getCartId());
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
            List<CartResponseDTO> cartList2 = cartService.getCartOfUserId(userId);
            generalResponse.setCode(HttpStatus.OK.value());
            generalResponse.setMsg("List of items in the cart");
            HashMap<String, List<CartResponseDTO>> cartItems = new HashMap<>();
            cartItems.put("cart_items", cartList2);
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


    @GetMapping("/products")
    public ResponseEntity<GeneralResponse> getAllProducts(@RequestParam(name = "offset") Integer offset, @RequestParam(name = "limit") Integer limit, @RequestParam(name = "user_id") Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{

            Integer totalCount = productService.findTotalCount();

            List<Product> allProducts = productService.findAllProductsPaginated(limit, offset);
            List<ProductResponseDTO> response=new ArrayList<>();

            allProducts.stream().forEach((product)->{
                List<Attachment> attachments=attachmentService.getAttachmentsByProductId(product.getId());
                ProductResponseDTO responseDTO=ProductResponseDTO.builder()
                        .id(product.getId())
                        .actualPrice(product.getActualPrice())
                        .discountedPrice(product.getDiscountedPrice())
                        .category(product.getCategory())
                        .createdAt(product.getCreatedAt())
                        .ownedBy(product.getOwnedBy())
                        .deliveryTime(product.getDeliveryTime())
                        .title(product.getTitle())
                        .titleDescription(product.getTitleDescription())
                        .description(product.getDescription())
                        .dimensions(product.getDimensions())
                        .rating(product.getRating())
                        .review(product.getReview())
                        .soldQuantity(product.getSoldQuantity())
                        .totalQuantity(product.getTotalQuantity())
                        .availableQuantity(product.getAvailableQuantity())
                        .weight(product.getWeight())
                        .attachments(attachments)
                        .build();
                response.add(responseDTO);
            });
            HashMap<String, Object> data = new HashMap<>();
            HashMap<String, Object> paginationData = new HashMap<>();

//            data.put("products", allProducts);
            data.put("products", response);
            paginationData.put("totalCount", totalCount);
            paginationData.put("productsPageCount", allProducts.size());
            paginationData.put("limit", limit);
            paginationData.put("offset", offset);
            data.put("pagination", paginationData);
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("List of Products Received");
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<GeneralResponse> getProduct(@PathVariable("id") Long productId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{

            HashMap<String, Object> data = new HashMap<>();
            Product product = productService.findProduct(productId);
            List<Attachment> attachments=attachmentService.getAttachmentsByProductId(productId);
            ProductResponseDTO responseDTO=ProductResponseDTO.builder()
                    .id(product.getId())
                    .actualPrice(product.getActualPrice())
                    .discountedPrice(product.getDiscountedPrice())
                    .category(product.getCategory())
                    .createdAt(product.getCreatedAt())
                    .ownedBy(product.getOwnedBy())
                    .deliveryTime(product.getDeliveryTime())
                    .title(product.getTitle())
                    .titleDescription(product.getTitleDescription())
                    .description(product.getDescription())
                    .dimensions(product.getDimensions())
                    .rating(product.getRating())
                    .review(product.getReview())
                    .soldQuantity(product.getSoldQuantity())
                    .totalQuantity(product.getTotalQuantity())
                    .availableQuantity(product.getAvailableQuantity())
                    .weight(product.getWeight())
                    .attachments(attachments)
                    .build();

            data.put("product", responseDTO);
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("Product with id: "+productId);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/order-checkout")
    public ResponseEntity<?> addOrder(@RequestBody OrderRequestDTO requestDTO ){
        log.info("checkout came: {}",requestDTO);
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<OrderResponseDTO> orderList = orderService.saveOrderItems(requestDTO);
//            List<OrderResponseDTO> cartList2 = cartService.getCartOfUserId(userId);
            generalResponse.setCode(HttpStatus.OK.value());
            generalResponse.setMsg("List of items in the cart");
            HashMap<String, List<OrderResponseDTO>> orderItems = new HashMap<>();
            orderItems.put("order_items", orderList);
            generalResponse.setData(orderItems);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Cannot create orders for this user");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(@RequestParam(name = "user_id") Long userId ){
        log.info("request reached for getting order for: {}",userId);
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<OrderResponseDTO> orderList = orderService.getOrderItems(userId);
            generalResponse.setCode(HttpStatus.OK.value());
            generalResponse.setMsg("List of items in the cart");
            HashMap<String, List<OrderResponseDTO>> orderItems = new HashMap<>();
            orderItems.put("order_items", orderList);
            generalResponse.setData(orderItems);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Cannot fetch Orders for this user");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
