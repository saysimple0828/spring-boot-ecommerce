package com.saysimple.axon.coreapi.queries;

import java.util.Objects;

public record TotalProductsShippedQuery(String productId) {

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TotalProductsShippedQuery that = (TotalProductsShippedQuery) o;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public String toString() {
        return "TotalProductsShippedQuery{" + "productId='" + productId + '\'' + '}';
    }
}
