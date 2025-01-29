package com.saysimple.products.aop;

import lombok.Getter;

@Getter
public enum ProductError {
    ProductNotFound("상품을 찾을 수 없습니다."),
    ;

    private final String msg;

    ProductError(String msg){
        this.msg = msg;
    }
}
