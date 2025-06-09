package com.example.OnlineRetailManagement.service;

import com.example.OnlineRetailManagement.DTO.ProductRequestDTO;
import com.example.OnlineRetailManagement.entity.Attachment;
import com.example.OnlineRetailManagement.entity.Product;
import com.example.OnlineRetailManagement.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class ProductService{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AttachmentService attachmentService;

    public List<Product> findAllProductsPaginated(Integer limit, Integer offset){
        return productRepository.findAllProductsPaginated(limit, offset);
    }

    public List<Product> findAllProductsPaginatedForVendor(Integer limit, Integer offset,Long userId){
        return productRepository.findAllProductsPaginatedForVendor(limit, offset,userId);
    }

    public Product findProduct(Long productId){
        return productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException("Product not found with id: " + productId));
    }

    public Product addProduct(ProductRequestDTO productDTO){
        Product newProduct=new Product();
        newProduct.setTitle(productDTO.getTitle());
        newProduct.setTitleDescription(productDTO.getTitleDescription());
        newProduct.setDescription(productDTO.getDescription());
        newProduct.setActualPrice(productDTO.getActualPrice());
        newProduct.setOwnedBy(productDTO.getOwnedBy());
        newProduct.setWeight(productDTO.getWeight());
        newProduct.setCategory(productDTO.getCategory());
        newProduct.setDimensions(productDTO.getDimensions());
        newProduct.setDiscountedPrice(productDTO.getDiscountedPrice());
        newProduct.setTotalQuantity(productDTO.getTotalQuantity());
        newProduct.setAvailableQuantity(productDTO.getAvailableQuantity());
        newProduct.setDeliveryTime(productDTO.getDeliveryTime());
        newProduct.setSoldQuantity(productDTO.getSoldQuantity());
        newProduct.setCreatedAt(LocalDateTime.now());
        log.info("product created to be saved: {}",newProduct);

        Product savedProduct=productRepository.save(newProduct);

        productDTO.getAttachmentIds().stream().forEach((id)->{
            Attachment attachment=attachmentService.getAttachmentById(id);
            attachment.setProductId(savedProduct.getId());
            attachmentService.saveAttachment(attachment);
        });


        return savedProduct;
    }

    public Product addProduct(Product newProduct){
        return productRepository.save(newProduct);
    }

    public Integer findTotalCount() {
        return productRepository.findTotalCount();
    }

    public Integer findTotalCountForVendor(Long userId) {
        return productRepository.findTotalCountForVendor(userId);
    }

    public Product updateProduct(Long productId,ProductRequestDTO requestDTO){

        Product product=this.findProduct(productId);

        product.setTitle(requestDTO.getTitle());
        product.setTitleDescription(requestDTO.getTitleDescription());
        product.setOwnedBy(requestDTO.getOwnedBy());
//        product.setCreatedAt(requestDTO.getCreatedAt());
        product.setCategory(requestDTO.getCategory());
//        product.setImageURLs(requestDTO.getImageURLs());
        product.setActualPrice(requestDTO.getActualPrice());
        product.setDiscountedPrice(requestDTO.getDiscountedPrice());
        product.setTotalQuantity(requestDTO.getTotalQuantity());
        product.setAvailableQuantity(requestDTO.getAvailableQuantity());
        product.setDescription(requestDTO.getDescription());
        product.setWeight(requestDTO.getWeight());
        product.setDimensions(requestDTO.getDimensions());
        product.setDeliveryTime(requestDTO.getDeliveryTime());
//        product.setRating(requestDTO.getRating());
//        product.setReview(requestDTO.getReview());
        product.setSoldQuantity(requestDTO.getSoldQuantity());

        return productRepository.save(product);
    }
    public void deleteProduct(Long productId){
        Product product=this.findProduct(productId);
        productRepository.delete(product);
    }

}
