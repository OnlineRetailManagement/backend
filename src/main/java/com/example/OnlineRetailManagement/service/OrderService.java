package com.example.OnlineRetailManagement.service;


import com.example.OnlineRetailManagement.DTO.OrderRequestDTO;
import com.example.OnlineRetailManagement.DTO.OrderResponseDTO;
import com.example.OnlineRetailManagement.DTO.ProductResponseDTO;
import com.example.OnlineRetailManagement.entity.*;
import com.example.OnlineRetailManagement.repository.CartRepository;
import com.example.OnlineRetailManagement.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private CartService cartService;

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
                    cartService.deleteCartByUserId(checkoutItem.getCartId());
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

    public List<OrderResponseDTO> getOrderItems(Long userId, Boolean isActive) {

        log.info("Getting order items for: {}",userId);
        List<Order> orders= orderRepository.findByUserId(userId);
        log.info("Orders came for userId: {} {}",userId,orders.toString());
        List<OrderResponseDTO> response=new ArrayList<>();

        orders=orders.stream()
                .filter(order -> {
                    if (isActive) {
                        return !order.getOrderStatus().equalsIgnoreCase("delivered");
                    } else {
                        return order.getOrderStatus().equalsIgnoreCase("delivered");
                    }
                })
                .collect(Collectors.toList());

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

    public List<OrderResponseDTO> getOrderItemsForVendor(Long userId, Boolean isActive) {

        log.info("Getting order items for: {}",userId);
        List<Order> orders= orderRepository.findByVendorId(userId);
        log.info("Orders came for userId: {} {}",userId,orders.toString());
        List<OrderResponseDTO> response=new ArrayList<>();

        orders=orders.stream()
                .filter(order -> {
                    if (isActive) {
                        return !order.getOrderStatus().equalsIgnoreCase("delivered");
                    } else {
                        return order.getOrderStatus().equalsIgnoreCase("delivered");
                    }
                })
                .collect(Collectors.toList());

        log.info("Orders after filtering is_active: {} {}",isActive,orders);

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

    public OrderResponseDTO updateOrderItemForVendor(Long orderId, String orderStatus) {

        log.info("Getting order items for: {}",orderId);
        Order order= orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order not found with id: " + orderId));
        order.setOrderStatus(orderStatus);
        Order updatedOrder=orderRepository.save(order);
        log.info("Orders came for order Id: {} {}",orderId,updatedOrder);

        Product product= productService.findProduct(updatedOrder.getProductId());
        log.info("Product Found for product id: {} {} ",updatedOrder.getProductId(), product);
        List<Attachment> attachments=attachmentService.getAttachmentsByProductId(updatedOrder.getProductId());
        log.info("Attachments found for product id: {} {} ",updatedOrder.getProductId(),attachments.toString());
        log.info("Finding address for: {} ",updatedOrder.getAddressId());
        Address address= addressService.getAddressById(updatedOrder.getAddressId());
        log.info("address found for address id: {} {}",updatedOrder.getAddressId(),address);

        log.info("Finding address for: {} ",updatedOrder.getAddressId());
        Payment payment= paymentService.getPayment(updatedOrder.getPaymentId());
        log.info("address found for address id: {} {}",updatedOrder.getAddressId(),address);

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
                .userId(updatedOrder.getUserId())
                .orderId(updatedOrder.getId())
                .checkoutDate(updatedOrder.getCheckoutDate())
                .product(productResponseDTO)
                .quantity(updatedOrder.getQuantity())
                .orderStatus(updatedOrder.getOrderStatus())
                .address(address)
                .paymentInfo(payment)
                .build();

        return  orderResponseDTO;
    }

    public Integer findTotalCountOrders(){
        return orderRepository.findTotalCountOrders();
    }

    public Integer getTotalRevenue(){
        Integer totalRevenue = orderRepository.getTotalRevenue();
        return totalRevenue;
    }

    public Integer getTotalDiscountedRevenue(){
        Integer discountedTotalRevenue = orderRepository.getTotalRevenueDiscounted();
        return discountedTotalRevenue;
    }

    public Integer getCountProcessing(){
        Integer countProcessing = orderRepository.getCountProcessing();
        return countProcessing;
    }

    public Integer getCountConfirmed(){
        Integer countConfirmed = orderRepository.getCountConfirmed();
        return countConfirmed;
    }

    public Integer getCountShipped(){
        Integer countShipped = orderRepository.getCountShipped();
        return countShipped;
    }

    public Integer getCountInTransit(){
        Integer countInTransit = orderRepository.getCountInTransit();
        return countInTransit;
    }

    public Integer getCountOutForDelivery(){
        Integer countOutForDelivery = orderRepository.getCountOutForDelivery();
        return countOutForDelivery;
    }

    public Integer getCountInDelivered(){
        Integer countInDelivered = orderRepository.getCountInDelivered();
        return countInDelivered;
    }

    public Integer getCustomerCount(Long vendorId){
        Integer customerCount = orderRepository.getCustomerCount(vendorId);
        return customerCount;
    }

    public Integer getTotalRevenueVendor(Long vendorId){
        Integer totalRevenueVendor = orderRepository.getTotalRevenueVendor(vendorId);
        return totalRevenueVendor;
    }

    public Integer getTotalRevenueDiscountedVendor(Long vendorId){
        Integer totalRevenueDiscountedVendor = orderRepository.getTotalRevenueDiscountedVendor(vendorId);
        return totalRevenueDiscountedVendor;
    }

    public Integer getCountProcessingVendor(Long vendorId){
        Integer countProcessingVendor = orderRepository.getCountProcessingVendor(vendorId);
        return countProcessingVendor;
    }

    public Integer getCountConfirmedVendor(Long vendorId){
        Integer countConfirmedVendor = orderRepository.getCountConfirmedVendor(vendorId);
        return countConfirmedVendor;
    }

    public Integer getCountShippedVendor(Long vendorId){
        Integer countShippedVendor = orderRepository.getCountShippedVendor(vendorId);
        return countShippedVendor;
    }

    public Integer getCountInTransitVendor(Long vendorId){
        Integer countInTransitVendor = orderRepository.getCountInTransitVendor(vendorId);
        return countInTransitVendor;
    }

    public Integer getCountOutForDeliveryVendor(Long vendorId){
        Integer countOutForDeliveryVendor = orderRepository.getCountOutForDeliveryVendor(vendorId);
        return countOutForDeliveryVendor;
    }

    public Integer getCountInDeliveredVendor(Long vendorId){
        Integer countInDeliveredVendor = orderRepository.getCountInDeliveredVendor(vendorId);
        return countInDeliveredVendor;
    }

    public Integer findTotalCartCount(){
        Integer totalCartCount = orderRepository.findTotalCartCount();
        return totalCartCount;
    }

    public Integer getCountCartVendor(Long vendorId){
        Integer countCartVendor = orderRepository.getCountCartVendor(vendorId);
        return countCartVendor;
    }

    public Integer getCountOrderVendor(Long vendorId){
        Integer countOrderVendor = orderRepository.getCountOrderVendor(vendorId);
        return countOrderVendor;
    }
}
