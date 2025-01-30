package com.saysimple.products.service;

import com.saysimple.products.entity.Product;
import com.saysimple.products.repository.ProductRepository;
import com.saysimple.products.vo.ProductRequest;
import com.saysimple.products.vo.ProductResponse;
import com.saysimple.products.vo.ProductUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.saysimple.error.ErrorCode;
import org.saysimple.error.exception.NotFoundException;
import org.saysimple.util.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    Environment env;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse create(ProductRequest product) {
        Product productEntity = ModelUtils.strictMap(product, Product.class);

        productEntity.setProductId(UUID.randomUUID().toString());
        productRepository.save(productEntity);

        return ModelUtils.map(productEntity, ProductResponse.class);
    }

    @Override
    public List<ProductResponse> list() {
        List<Product> productEntities = (List<Product>) productRepository.findAll();

        return productEntities.stream()
                .map(entity -> ModelUtils.map(entity, ProductResponse.class))
                .toList();
    }


    @Override
    public ProductResponse get(String productId) {
        Product product = productRepository.findByProductId(productId).orElseThrow(() ->
                new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        return ModelUtils.map(product, ProductResponse.class);
    }

    @Override
    public ProductResponse update(ProductUpdateRequest product) {
        Product productEntity = productRepository.findByProductId(product.getProductId()).orElseThrow(() ->
                new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        productEntity.setName(product.getName());
        productEntity.setCategoryId(product.getCategoryId());
        productRepository.save(productEntity);

        return ModelUtils.map(productEntity, ProductResponse.class);
    }

    @Override
    public void delete(String productId) {
        Product product = productRepository.findByProductId(productId).orElseThrow(() ->
                new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    }
}
