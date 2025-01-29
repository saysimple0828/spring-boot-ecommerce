package com.saysimple.products.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {
    @NotNull(message = "name cannot be null")
    @Size(max = 255, message = "Name must be less then 255 characters")
    private String name;

    @NotNull(message = "categoryId cannot be null")
    @Size(min = 8, message = "Password must be equal or grater than 8 characters")
    private String categoryId;
}
