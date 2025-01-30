package com.saysimple.products.service;

import com.saysimple.products.vo.ProductRequest;
import com.saysimple.products.vo.ProductResponse;
import com.saysimple.products.vo.ProductUpdateRequest;

import java.util.List;

public interface ProductService {


    ProductResponse create(ProductRequest product);

    List<ProductResponse> list();

    ProductResponse get(String productId);

    ProductResponse update(ProductUpdateRequest product);

    void delete(String productId);
}
