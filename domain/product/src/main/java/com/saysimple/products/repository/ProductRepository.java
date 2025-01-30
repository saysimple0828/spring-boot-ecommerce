package com.saysimple.products.repository;

import com.saysimple.products.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {
    Optional<Product> findByProductId(String productId);
}
