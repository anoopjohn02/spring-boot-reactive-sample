package com.anoop.examples.auth;

import com.anoop.examples.model.IotoUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AuthService {

    @Value("${ioto.login.url}")
    private String authUrl;

    @Value("${ioto.login.userId}")
    private String deviceId;

    @Value("${ioto.login.password}")
    private String devicePassword;

    @Value("${ioto.login.clientId}")
    private String clientId;

    @Value("${ioto.account.ms.url}")
    private String accountUrl;

    private DeviceAccount deviceAccount;

    /**
     * Method to get toke of the device.
     * If the token not available or expired, it will authenticate first.
     *
     * @return the token
     */
    public String getDeviceToken(){
        if(deviceAccount == null ||
           !validateToken(deviceAccount.getAccess_token())) {
            authenicate();
        }
        return deviceAccount.getAccess_token();
    }

    private void authenicate(){

        AuthForm authForm = new AuthForm();
        authForm.setGrant_type("password");
        authForm.setClient_id(clientId);
        authForm.setUsername(deviceId);
        authForm.setPassword(devicePassword);

        HttpHeaders headers = createHeaders();
        HttpEntity<AuthForm> request = new HttpEntity<>(authForm, headers);

        RestTemplate restTemplate = getRestTemplate();
        ResponseEntity<DeviceAccount> response = restTemplate.exchange(authUrl, HttpMethod.POST, request, DeviceAccount.class);
        this.deviceAccount = response.getBody();
    }

    private RestTemplate getRestTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(new ObjectMapper()));
        return restTemplate;
    }

    private HttpHeaders createHeaders() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return httpHeaders;
    }

    private boolean validateToken(String token){
        String url = accountUrl + "/user/info";

        HttpHeaders headers = createHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<AuthForm> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = getRestTemplate();
        try {
            ResponseEntity<IotoUser> response = restTemplate.exchange(url, HttpMethod.GET, request, IotoUser.class);
            if(response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
