package org.saysimple.error.exception;

import lombok.Getter;
import org.saysimple.error.ErrorCode;

@Getter
public class NotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}