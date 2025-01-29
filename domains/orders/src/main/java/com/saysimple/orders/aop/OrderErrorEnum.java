package com.saysimple.orders.aop;

import lombok.Getter;

@Getter
public enum OrderErrorEnum {
    ORDER_NOT_FOUND("주문 찾을 수 없습니다."),
    ;

    private final String msg;

    OrderErrorEnum(String msg){
        this.msg = msg;
    }
}
