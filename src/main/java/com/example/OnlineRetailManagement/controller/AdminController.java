package com.example.OnlineRetailManagement.controller;

import com.example.OnlineRetailManagement.DTO.ProductRequestDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @GetMapping("/health-check")
    public String healthCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Ok";
    }

    @GetMapping("/users")
    public ResponseEntity<GeneralResponse> getAllUsers(@RequestParam(name = "offset") Integer offset, @RequestParam(name = "limit") Integer limit){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{

            Integer totalCount = userService.findTotalCount();
            List<User> allUser = userService.findAllUsersPaginated(limit, offset);
            HashMap<String, Object> data = new HashMap<>();
            HashMap<String, Object> paginationData = new HashMap<>();

            data.put("users", allUser);
            paginationData.put("totalCount", totalCount);
            paginationData.put("usersPageCount", allUser.size());
            paginationData.put("limit", limit);
            paginationData.put("offset", offset);
            data.put("pagination", paginationData);
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("List of Users Received");
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/vendors")
    public ResponseEntity<GeneralResponse> getAllVendors(@RequestParam(name = "offset") Integer offset, @RequestParam(name = "limit") Integer limit){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{

            Integer totalCount = userService.findTotalCountVendors();
            List<User> allVendor = userService.findAllUsersPaginatedVendors(limit, offset);
            HashMap<String, Object> data = new HashMap<>();
            HashMap<String, Object> paginationData = new HashMap<>();

            data.put("vendors", allVendor);
            paginationData.put("totalCount", totalCount);
            paginationData.put("vendorsPageCount", allVendor.size());
            paginationData.put("limit", limit);
            paginationData.put("offset", offset);
            data.put("pagination", paginationData);
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("List of Vendors Received");
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<GeneralResponse> getAllProducts(@RequestParam(name = "offset") Integer offset, @RequestParam(name = "limit") Integer limit,@RequestParam(name = "user_id") Long userId){
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

    @GetMapping("/users/{id}")
    public ResponseEntity<GeneralResponse> getSingleUsers(@PathVariable("id") Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{

            HashMap<String, Object> data = new HashMap<>();
            Optional<User> user = userService.findById(userId);
            data.put("user", user);
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("User with id : "+userId);
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
    @PostMapping("/products")
    public ResponseEntity<GeneralResponse> addProduct(@RequestBody ProductRequestDTO requestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Request came at POST /product");
        log.info("request data: {}", requestDTO.toString());
        GeneralResponse generalResponse = new GeneralResponse();
        try{

            HashMap<String, Object> data = new HashMap<>();
            Product product = productService.addProduct(requestDTO);
            data.put("id", product.getId());
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("Product created Successfully!"+product.getId());
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<GeneralResponse> updateProduct(@PathVariable("id") Long productId, @RequestBody ProductRequestDTO requestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{

            HashMap<String, Object> data = new HashMap<>();
            Product updatedProduct = productService.updateProduct(productId,requestDTO);
            data.put("id", updatedProduct.getId());
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("Updated Product with id: "+productId);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/products/{id}")
    public ResponseEntity<GeneralResponse> deleteProduct(@PathVariable("id") Long productId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            productService.deleteProduct(productId);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("Deleted Product with id: "+productId);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<GeneralResponse> adminStatistics(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            Integer totalUserCount = userService.findTotalCountUsers();
            Integer totalVendorCount = userService.findTotalCountVendors();
            Integer totalOrderCount = orderService.findTotalCountOrders();
            Integer totalRevenue = orderService.getTotalRevenue();
            Integer discountedTotalRevenue = orderService.getTotalDiscountedRevenue();

            Integer countProcessing = orderService.getCountProcessing();
            Integer countConfirmed = orderService.getCountConfirmed();
            Integer countShipped = orderService.getCountShipped();
            Integer countInTransit = orderService.getCountInTransit();
            Integer countOutForDelivery = orderService.getCountOutForDelivery();
            Integer countInDelivered = orderService.getCountInDelivered();

            generalResponse.setMsg("Statistics fetched successfully");
            generalResponse.setCode(HttpStatus.OK.value());
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("total_users", totalUserCount);
            dataMap.put("total_vendors", totalVendorCount);
            dataMap.put("total_order_count", totalOrderCount);
            dataMap.put("total_revenue", totalRevenue);
            dataMap.put("total_discounted_revenue", discountedTotalRevenue);
            dataMap.put("count_processing", countProcessing);
            dataMap.put("count_confirmed", countConfirmed);
            dataMap.put("count_shipped", countShipped);
            dataMap.put("count_in_transit", countInTransit);
            dataMap.put("count_out_for_delivery", countOutForDelivery);
            dataMap.put("count_in_delivered", countInDelivered);


            generalResponse.setData(dataMap);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg("Cannot fetch statistics");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

}
