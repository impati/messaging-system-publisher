package com.example.impati.messaging_system_publisher.core;

import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

public class SimpleClientRegister implements ClientRegister {

    private final MessagingSystemClient client;

    public SimpleClientRegister(MessagingSystemClient client) {
        this.client = client;
    }

    public void register(String clientName) {
        client.get("/v1/client", "clientName", clientName)
                .flatMap(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
                        client.post("/v1/client", "clientName", clientName);
                    }
                    return Mono.empty();
                })
                .subscribe();
    }
}

