package org.saysimple.aop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException {
    private final Integer statusCode;
    private final String message;

    public ForbiddenException(String message) {
        super(message);
        this.statusCode = HttpStatus.FORBIDDEN.value();
        this.message = message;
    }
}
