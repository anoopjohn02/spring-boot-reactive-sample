package com.anoop.examples.config;

import com.anoop.examples.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

@Component
public class HeaderInterceptor implements ClientHttpRequestInterceptor {
    public static final String AUTH_HEADER = "Authorization";

    @Autowired
    private AuthService authService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        String token = "";
        if(requestAttributes == null){
            token = "Bearer " + authService.getDeviceToken();
        } else {
            token = requestAttributes.getRequest().getHeader(AUTH_HEADER);
        }
        HttpHeaders headers = request.getHeaders();
        headers.add(AUTH_HEADER, token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }
}
