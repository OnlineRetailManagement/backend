package com.example.OnlineRetailManagement.service;


import com.example.OnlineRetailManagement.repository.ProductRepository;
import com.example.OnlineRetailManagement.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
}
