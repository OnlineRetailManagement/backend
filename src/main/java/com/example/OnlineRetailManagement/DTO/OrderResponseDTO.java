package com.example.OnlineRetailManagement.DTO;

import com.example.OnlineRetailManagement.entity.Address;
import com.example.OnlineRetailManagement.entity.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderResponseDTO {

    @JsonProperty("id")
    private Long orderId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("product")
    private ProductResponseDTO product;

    @JsonProperty("quantity")
    private Integer quantity = 1;

    @JsonProperty("checkout_date")
    private LocalDateTime checkoutDate;

    @JsonProperty("order_status")
    private String orderStatus;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("payment_info")
    private Payment paymentInfo;
}
