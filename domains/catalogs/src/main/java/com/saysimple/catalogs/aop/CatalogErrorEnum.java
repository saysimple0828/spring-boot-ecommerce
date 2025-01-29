package com.saysimple.catalogs.aop;

import lombok.Getter;

@Getter
public enum CatalogErrorEnum {
    CATALOG_NOT_FOUND("카탈로그를 찾을 수 없습니다."),
    ;

    private final String msg;

    CatalogErrorEnum(String msg){
        this.msg = msg;
    }
}
