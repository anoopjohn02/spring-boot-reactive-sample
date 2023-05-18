package com.anoop.examples.config;

import com.anoop.examples.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class IotoExchangeFilter implements ExchangeFilterFunction {

    public static final String AUTH_HEADER = "Authorization";

    @Autowired
    private AuthService authService;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = "";
        if(requestAttributes == null){
            token = "Bearer " + authService.getDeviceToken();
        } else {
            token = requestAttributes.getRequest().getHeader(AUTH_HEADER);
        }

        ClientRequest filtered = ClientRequest.from(request)
                .header(AUTH_HEADER, token)
                .build();
        return next.exchange(filtered);
    }

    @Override
    public ExchangeFilterFunction andThen(ExchangeFilterFunction afterFilter) {
        return ExchangeFilterFunction.super.andThen(afterFilter);
    }

    @Override
    public ExchangeFunction apply(ExchangeFunction exchange) {
        return ExchangeFilterFunction.super.apply(exchange);
    }
}
