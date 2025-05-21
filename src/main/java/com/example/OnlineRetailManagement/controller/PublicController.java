package com.example.OnlineRetailManagement.controller;

import com.example.OnlineRetailManagement.entity.GeneralResponse;
import com.example.OnlineRetailManagement.entity.User;
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
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "Ok";
    }

    @PostMapping("/signup")
    public void signup(@RequestBody User user) {
        userService.saveNewUser(user);
    }

    @PostMapping("/signup-vendor")
    public void signupVendor(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService.saveVendor(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            HashMap<String, String> data = new HashMap<>();
            data.put("jwt", jwt);
            generalResponse.setData(data);
            String statusCode = String.valueOf(HttpStatus.OK.value());
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("Login Successful!");
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login-vendor")
    public ResponseEntity<String> loginVendor(@RequestBody User user) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login-admin")
    public ResponseEntity<String> loginAdmin(@RequestBody User user) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }
}
