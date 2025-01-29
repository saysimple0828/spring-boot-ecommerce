package com.saysimple.users.aop.vo;

public record ErrorResponse(boolean result, Integer status, String message) {
    public ErrorResponse(Integer status, String message){
        this(false, status, message);
    }
}
