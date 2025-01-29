package com.saysimple.reviews.aop;

import lombok.Getter;

@Getter
public enum ReviewErrorEnum {
    REVIEW_NOT_FOUND("리뷰를 찾을 수 없습니다."),
    ;

    private final String msg;

    ReviewErrorEnum(String msg){
        this.msg = msg;
    }
}
