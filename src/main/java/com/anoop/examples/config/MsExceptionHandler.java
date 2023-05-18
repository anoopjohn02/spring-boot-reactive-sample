package com.anoop.examples.config;

import com.anoop.examples.exceptions.MSRuntimeException;
import com.anoop.examples.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class MsExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MsExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex,
                                         HttpServletRequest request, HttpServletResponse response) {
        if (ex instanceof MSRuntimeException) {
            MSRuntimeException msRuntimeException = (MSRuntimeException) ex;

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(true);
            errorResponse.setErrorMessage(msRuntimeException.getErrorMessage());
            errorResponse.setHttpStatus(msRuntimeException.getHttpStatus());
            errorResponse.setHttpStatusCode(msRuntimeException.getHttpStatus().value());
            errorResponse.setMessageKey(msRuntimeException.getErrorCode());

            return new ResponseEntity<>(errorResponse, msRuntimeException.getHttpStatus());
        }
        logger.error("Internal Server Error in Hub ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
