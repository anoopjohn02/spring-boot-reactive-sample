package com.anoop.examples.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends MSRuntimeException {

    private static final long serialVersionUID = 1L;

    private final Throwable cause;

    public InternalServerException(final Throwable cause) {
        super(ErrorCodes.INTERNAL_SERVER_ERROR);
        this.cause = cause;

    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
