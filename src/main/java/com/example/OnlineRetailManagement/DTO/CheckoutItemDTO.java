package com.example.OnlineRetailManagement.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CheckoutItemDTO {
    @JsonProperty("cart_id")
    private Long cartId;

    @JsonProperty("product_id")
    private Long productId;

    private Integer quantity;

    @JsonProperty("order_status")
    private String orderStatus;
}
