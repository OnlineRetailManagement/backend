package com.example.OnlineRetailManagement.service;

import com.example.OnlineRetailManagement.entity.Product;
import com.example.OnlineRetailManagement.entity.User;
import com.example.OnlineRetailManagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProductService{
    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAllProductsPaginated(Integer limit, Integer offset){
        return productRepository.findAllProductsPaginated(limit, offset);
    }

    public Optional<Product> findProduct(Long productId){
        return productRepository.findById(productId);
    }

    public Product addProduct(Product newProduct){
        return productRepository.save(newProduct);
    }

    public Integer findTotalCount() {
        return productRepository.findTotalCount();
    }

//    public Product updateProduct(Product newproduct){
//        return productRepository.save(newproduct);
//    }
//    public Product deleteProduct(Product newproduct){
//        return productRepository.save(newproduct);
//    }

}
