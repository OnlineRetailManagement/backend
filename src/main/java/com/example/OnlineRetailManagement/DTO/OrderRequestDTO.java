package com.example.OnlineRetailManagement.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderRequestDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("address_id")
    private Long addressId;

    @JsonProperty("payment_id")
    private Long paymentId;

    @JsonProperty("checkout_items")
    private List<CheckoutItemDTO> checkoutItems;
}
