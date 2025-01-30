package org.saysimple.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 공통 에러
    INVALID_INPUT(400, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 내부 오류"),

    /** 비즈니스 도메인별 에러 */
    // 유저 에러
    USER_NOT_FOUND(404, "U001", "유저를 찾을 수 없음"),
    // 주문 에러
    ORDER_NOT_FOUND(404, "O001", "주문을 찾을 수 없음"),
    // 상품 에러
    PRODUCT_NOT_FOUND(404, "P001", "상품을 찾을 수 없음"),
    // 리뷰 에러
    REVIEW_NOT_FOUND(404, "O001", "리뷰를 찾을 수 없음"),
    ;

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}