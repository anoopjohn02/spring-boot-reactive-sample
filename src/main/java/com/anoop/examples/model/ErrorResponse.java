package com.anoop.examples.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse {

    public boolean error;
    public String errorMessage;
    public String messageKey;
    public HttpStatus httpStatus;
    public int httpStatusCode;
}
