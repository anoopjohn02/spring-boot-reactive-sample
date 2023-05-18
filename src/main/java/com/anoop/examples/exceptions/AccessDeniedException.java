package com.anoop.examples.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccessDeniedException extends MSRuntimeException {
    private static final long serialVersionUID = 1L;

    public AccessDeniedException(ErrorCodes errorCodes){
        super(errorCodes);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
