package com.saysimple.reviews.error;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    ReviewNotFound("Review not found");

    private final String message;

    ErrorEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
