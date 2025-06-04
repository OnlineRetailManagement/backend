package com.example.OnlineRetailManagement.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductRequestDTO {
    private String title;
    @JsonProperty("title_description")
    private String titleDescription;
    private String description;
    @JsonProperty("attachment_ids")
    private List<Long> attachmentIds;
    @JsonProperty("actual_price")
    private Integer actualPrice;
    @JsonProperty("owned_by")
    private Long ownedBy;
    private String category;
    @JsonProperty("discounted_price")
    private Integer discountedPrice;
    @JsonProperty("total_quantity")
    private Integer totalQuantity;
    @JsonProperty("available_quantity")
    private Integer availableQuantity;
    private Double weight;
    private String dimensions;
    @JsonProperty("delivery_time")
    private String deliveryTime;
    @JsonProperty("sold_quantity")
    private Integer soldQuantity;

}
