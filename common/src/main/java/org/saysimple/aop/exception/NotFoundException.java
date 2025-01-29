package org.saysimple.aop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException {
    private final Integer statusCode;
    private final String message;

    public NotFoundException(String message) {
        super(message);
        this.statusCode = HttpStatus.NOT_FOUND.value();
        this.message = message;
    }
}
