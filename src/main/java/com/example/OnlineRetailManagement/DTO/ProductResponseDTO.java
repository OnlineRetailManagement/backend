package com.example.OnlineRetailManagement.DTO;

import com.example.OnlineRetailManagement.entity.Attachment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductResponseDTO {

    @JsonProperty("id")
    private Long id;
    private String title;
    @JsonProperty("title_description")
    private String titleDescription;
    private String description;
    @JsonProperty("attachment_ids")
    private List<Attachment> attachments;
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
    @JsonProperty("created_at")
    private Date createdAt;
    private Double rating;
    private String review;
}
