package com.saysimple.reviews.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull(message = "productId cannot be null")
    private String productId;

    @NotNull(message = "categoryId cannot be null")
    private String categoryId;

    @NotNull(message = "userId cannot be null")
    private String userId;

    @NotNull(message = "title cannot be null")
    private String title;

    @NotNull(message = "content cannot be null")
    private String content;
}
