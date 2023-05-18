package com.anoop.examples.controllers;

import com.anoop.examples.config.InjectIotoUser;
import com.anoop.examples.model.IotoUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("v1/greetings")
public class GreetingController {

    @GetMapping
    public Mono<String> greeting() {
        return hello()
                .zipWith(from())
                .map(objects -> {
                   return objects.getT1() + " " + objects.getT2();
                });
    }

    @GetMapping(value = "/user")
    public Mono<String> userGreeting(@InjectIotoUser IotoUser user) {
        return Mono.zip(hello(), user(user), from())
                .map(objects -> {
                    return objects.getT1() + " " + objects.getT2() + " " + objects.getT3();
                });
    }

    private Mono<String> hello() {
        return Mono.just("Hello").delayElement(Duration.ofSeconds(5));
    }

    private Mono<String> from() {
        return Mono.just("from server").delayElement(Duration.ofSeconds(5));
    }

    private Mono<String> user(IotoUser user) {
        return Mono.just(user.getUserName()).delayElement(Duration.ofSeconds(5));
    }
}
