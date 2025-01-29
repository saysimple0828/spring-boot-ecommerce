package org.saysimple.aop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnAuthorizeException extends RuntimeException {
    private final Integer statusCode;
    private final String message;

    public UnAuthorizeException(String message) {
        super(message);
        this.statusCode = HttpStatus.UNAUTHORIZED.value();
        this.message = message;
    }
}
