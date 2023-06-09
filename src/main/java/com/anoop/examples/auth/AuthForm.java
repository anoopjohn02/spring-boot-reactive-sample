package com.anoop.examples.auth;

import lombok.Data;

@Data
public class AuthForm {
    private String grant_type;
    private String username;
    private String password;
    private String client_id;
    private String client_secret;
    private String refresh_token;
}
