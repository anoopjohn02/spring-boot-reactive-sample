package com.anoop.examples.auth;

import lombok.Data;

@Data
public class DeviceAccount {
    private String access_token;
    private String refresh_token;
    private int accountId;
    private long expires_in;
    private String token_type;
}
