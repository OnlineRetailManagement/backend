package com.example.OnlineRetailManagement.controller;

import com.example.OnlineRetailManagement.DTO.ProductRequestDTO;
import com.example.OnlineRetailManagement.DTO.ProductResponseDTO;
import com.example.OnlineRetailManagement.entity.Attachment;
import com.example.OnlineRetailManagement.entity.GeneralResponse;
import com.example.OnlineRetailManagement.entity.Product;
import com.example.OnlineRetailManagement.entity.User;
import com.example.OnlineRetailManagement.repository.UserRepository;
import com.example.OnlineRetailManagement.service.AttachmentService;
import com.example.OnlineRetailManagement.service.ProductService;
import com.example.OnlineRetailManagement.service.UserDetailsServiceImpl;
import com.example.OnlineRetailManagement.service.UserService;
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

@RestController
@Slf4j
@RequestMapping("/vendor")
public class VendorController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/health-check")
    public String healthCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Ok";
    }

    @GetMapping("/products")
    public ResponseEntity<GeneralResponse> getAllProducts(@RequestParam(name = "offset") Integer offset, @RequestParam(name = "limit") Integer limit,@RequestParam(name = "user_id") Long userId){
        log.info("request reached for POST /products for user ID: {}",userId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{

            Integer totalCount = productService.findTotalCountForVendor(userId);
            log.info("Total count of product for user ID: {} is {}",userId,totalCount);

            List<Product> allProducts = productService.findAllProductsPaginatedForVendor(limit, offset,userId);
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

//            HashMap<String, Object> data = new HashMap<>();
            productService.deleteProduct(productId);
//            data.put("product", product);
//            generalResponse.setData(data);
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
}
