package com.example.OnlineRetailManagement.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentRequestDTO {

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("product_id")
    private Long productId;
    private MultipartFile file;

}
