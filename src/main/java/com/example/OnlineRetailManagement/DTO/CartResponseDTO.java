package com.example.OnlineRetailManagement.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CartResponseDTO {

    @JsonProperty("id")
    private Long cartId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("product")
    private ProductResponseDTO product;

    @JsonProperty("quantity")
    private Integer quantity = 1;

    @JsonProperty("created_date")
    private LocalDateTime createdDate;
}
