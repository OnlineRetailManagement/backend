package com.example.OnlineRetailManagement.service;

import com.example.OnlineRetailManagement.DTO.CartResponseDTO;
import com.example.OnlineRetailManagement.DTO.ProductResponseDTO;
import com.example.OnlineRetailManagement.entity.Attachment;
import com.example.OnlineRetailManagement.entity.Cart;
import com.example.OnlineRetailManagement.entity.Product;
import com.example.OnlineRetailManagement.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private AttachmentService attachmentService;

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public List<Cart> getCartByUserId(Long userid){
        return cartRepository.getCartByUserId(userid);
    }

    public List<CartResponseDTO> getCartOfUserId(Long userid){
        List<CartResponseDTO> cartResponse=new ArrayList<>();
        List<Cart> cartList= this.cartRepository.getCartByUserId(userid);
        cartList.stream().forEach((cart -> {

            Product product = productService.findProduct(cart.getProductId());
            List<Attachment> attachments=attachmentService.getAttachmentsByProductId(cart.getProductId());
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

            CartResponseDTO cartResponseDTO=CartResponseDTO.builder()
                    .userId(cart.getUserId())
                    .cartId(cart.getCartId())
                    .product(responseDTO)
                    .quantity(cart.getQuantity())
                    .createdDate(cart.getCreatedDate())
                    .build();

            cartResponse.add(cartResponseDTO);


        }));

        return cartResponse;
//        return cartRepository.getCartByUserId(userid);
    }

    public void deleteCartByUserId(Long cartId){
        cartRepository.deleteById(cartId);
    }

    public Cart getCartByCartId(Long cartId){
        return cartRepository.getReferenceById(cartId);
    }
}
