package com.anoop.examples.exceptions;

import org.springframework.http.HttpStatus;

public abstract class MSRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCodes errorCode;

    public MSRuntimeException(final ErrorCodes errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public String getErrorCode(){
        return errorCode.getKey();
    }

    public String getErrorMessage(){
        return errorCode.getDescription();
    }

    public abstract HttpStatus getHttpStatus();
}
