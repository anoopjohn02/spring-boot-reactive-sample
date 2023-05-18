package com.anoop.examples.exceptions;

public enum ErrorCodes {

    ACCOUNT_NOT_EXIST("server.error.response.account.not.exist","Account Not Exist"),
    ALERT_NOT_FOUND("server.error.response.alert.not.found","Alert Not Found"),
    MEASUREMENT_NOT_FOUND("server.error.response.measurement.not.found","Measurement Not Found"),
    COMPANY_NOT_EXIST("server.error.response.company.not.exist","Company Not Exist"),
    ACCESS_DENIED("server.error.response.access.denied","Access Denied"),
    INTERNAL_SERVER_ERROR("server.error.response.internal.server.error","Internal Server Error");

    private final String key;
    private final String description;

    private ErrorCodes(String key, String description){
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
