package com.example.OnlineRetailManagement.controller;

import com.example.OnlineRetailManagement.entity.GeneralResponse;
import com.example.OnlineRetailManagement.entity.Pagination;
import com.example.OnlineRetailManagement.entity.Product;
import com.example.OnlineRetailManagement.entity.User;
import com.example.OnlineRetailManagement.repository.UserRepository;
import com.example.OnlineRetailManagement.service.ProductService;
import com.example.OnlineRetailManagement.service.UserDetailsServiceImpl;
import com.example.OnlineRetailManagement.service.UserService;
import com.example.OnlineRetailManagement.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
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
    private UserRepository userRepository;

    @GetMapping("/health-check")
    public String healthCheck() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Ok";
    }

    @GetMapping("/users")
    public ResponseEntity<GeneralResponse> getAllUsers(@RequestBody Pagination pagination){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            int limit = pagination.getLimit();
            int offset = pagination.getOffset();

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

    @GetMapping("/products")
    public ResponseEntity<GeneralResponse> getAllProducts(@RequestBody Pagination pagination){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            int limit = pagination.getLimit();
            int offset = pagination.getOffset();

            Integer totalCount = productService.findTotalCount();

            List<Product> allProducts = productService.findAllProductsPaginated(limit, offset);
            HashMap<String, Object> data = new HashMap<>();
            HashMap<String, Object> paginationData = new HashMap<>();

            data.put("products", allProducts);
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

}
