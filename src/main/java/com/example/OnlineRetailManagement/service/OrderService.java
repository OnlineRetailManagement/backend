package com.example.OnlineRetailManagement.service;


import com.example.OnlineRetailManagement.DTO.OrderRequestDTO;
import com.example.OnlineRetailManagement.DTO.OrderResponseDTO;
import com.example.OnlineRetailManagement.DTO.ProductResponseDTO;
import com.example.OnlineRetailManagement.entity.*;
import com.example.OnlineRetailManagement.repository.CartRepository;
import com.example.OnlineRetailManagement.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AttachmentService attachmentService;
    public List<OrderResponseDTO> saveOrderItems(OrderRequestDTO requestDTO) {
        log.info("inside saveOrderItems: {}",requestDTO);
        List<Order> savedOrders=new ArrayList<>();
        List<OrderResponseDTO> response=new ArrayList<>();

        requestDTO.getCheckoutItems()
                .stream()
                .forEach((checkoutItem) -> {
                    savedOrders.add(this.saveOrderItem(checkoutItem.getCartId(),
                            checkoutItem.getProductId(),
                            checkoutItem.getQuantity(),
                            requestDTO.getUserId(),
                            checkoutItem.getOrderStatus(),
                            requestDTO.getAddressId(),
                            requestDTO.getPaymentId()));
                });

        savedOrders.stream().forEach((order) -> {

            Product product= productService.findProduct(order.getProductId());
            List<Attachment> attachments=attachmentService.getAttachmentsByProductId(order.getProductId());
            Address address= addressService.getAddressById(requestDTO.getAddressId());
            Payment payment= paymentService.getPayment(requestDTO.getPaymentId());


            ProductResponseDTO productResponseDTO=ProductResponseDTO.builder()
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

            OrderResponseDTO orderResponseDTO=OrderResponseDTO.builder()
                    .userId(order.getUserId())
                    .orderId(order.getId())
                    .checkoutDate(order.getCheckoutDate())
                    .product(productResponseDTO)
                    .quantity(order.getQuantity())
                    .orderStatus(order.getOrderStatus())
                    .address(address)
                    .paymentInfo(payment)
                    .build();
            response.add(orderResponseDTO);
        });

        return  response;

    }

    private Order saveOrderItem(Long cartId, Long productId,
                                Integer quantity, Long userId,
                                String orderStatus, Long addressId, Long paymentId) {
        log.info("inside saveOrderItem");
        Order order=Order.builder()
                .cartId(cartId)
                .productId(productId)
                .orderStatus(orderStatus)
                .quantity(quantity)
                .checkoutDate(LocalDateTime.now())
                .userId(userId)
                .addressId(addressId)
                .paymentId(paymentId)
                .build();

        log.info("inside saveOrderItem: {}",order);
        Order savedOrder= orderRepository.save(order);
        log.info("saved order is: {}",savedOrder);
        return savedOrder;
    }

    public List<OrderResponseDTO> getOrderItems(Long userId) {

        log.info("Getting order items for: {}",userId);
        List<Order> orders= orderRepository.findByUserId(userId);
        log.info("Orders came for userId: {} {}",userId,orders.toString());
        List<OrderResponseDTO> response=new ArrayList<>();

        orders.stream().forEach((order) -> {

            Product product= productService.findProduct(order.getProductId());
            log.info("Product Found for product id: {} {} ",order.getProductId(), product);
            List<Attachment> attachments=attachmentService.getAttachmentsByProductId(order.getProductId());
            log.info("Attachments found for product id: {} {} ",order.getProductId(),attachments.toString());
            log.info("Finding address for: {} ",order.getAddressId());
            Address address= addressService.getAddressById(order.getAddressId());
            log.info("address found for address id: {} {}",order.getAddressId(),address);

            log.info("Finding address for: {} ",order.getAddressId());
            Payment payment= paymentService.getPayment(order.getPaymentId());
            log.info("address found for address id: {} {}",order.getAddressId(),address);

            ProductResponseDTO productResponseDTO=ProductResponseDTO.builder()
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

            OrderResponseDTO orderResponseDTO=OrderResponseDTO.builder()
                    .userId(order.getUserId())
                    .orderId(order.getId())
                    .checkoutDate(order.getCheckoutDate())
                    .product(productResponseDTO)
                    .quantity(order.getQuantity())
                    .orderStatus(order.getOrderStatus())
                    .address(address)
                    .paymentInfo(payment)
                    .build();
            response.add(orderResponseDTO);
        });
        return  response;
    }
}
