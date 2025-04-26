package com.example.impati.messaging_system_publisher.core;

import reactor.core.publisher.Mono;

public interface MessagingSystemClient {

    void post(String path, String queryParamName, Object queryParamValue);

    void post(String path, Object body);

    Mono<MessagingSystemResponse> get(String path);

    Mono<MessagingSystemResponse> get(String path, String queryParamName, Object queryParamValue);
}
