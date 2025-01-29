package com.saysimple.products.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private Long id;
    private String productId;
    private String name;
    private String categoryId;
    private String categoryName;
}
