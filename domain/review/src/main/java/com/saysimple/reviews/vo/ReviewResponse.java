package com.saysimple.reviews.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {
    private Long id;
    private String reviewId;
    private String productId;
    private String categoryId;
    private String userId;
    private String title;
    private String content;
    private Integer rating;
}
