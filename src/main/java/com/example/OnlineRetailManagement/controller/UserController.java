package com.example.OnlineRetailManagement.controller;

import com.example.OnlineRetailManagement.repository.UserRepository;
import com.example.OnlineRetailManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;




}
