package com.saysimple.reviews.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private String reviewId;
    private String productId;
    private String categoryId;
    private String userId;
    private String title;
    private String content;
    private Integer rating;
    private Integer goodCount;
    private Integer badCount;
}
