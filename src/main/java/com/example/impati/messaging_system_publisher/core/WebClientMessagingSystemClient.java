package com.example.impati.messaging_system_publisher.core;

import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class WebClientMessagingSystemClient implements MessagingSystemClient {

    private final WebClient webClient;

    public WebClientMessagingSystemClient(WebClient.Builder webClient, MessagingSystemPublisherProperties properties) {
        this.webClient = webClient.baseUrl(properties.url()).build();
    }

    public void post(String path, String queryParamName, Object queryParamValue) {
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam(queryParamName, queryParamValue)
                        .build())
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> Mono.empty())
                .retryWhen(
                        Retry.fixedDelay(3, Duration.ofSeconds(1))
                                .filter(throwable -> throwable instanceof WebClientResponseException
                                        && ((WebClientResponseException) throwable)
                                        .getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR))
                .subscribe();
    }

    public void post(String path, Object body) {
        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .build())
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> Mono.empty())
                .retryWhen(
                        Retry.fixedDelay(3, Duration.ofSeconds(1))
                                .filter(throwable -> throwable instanceof WebClientResponseException
                                        && ((WebClientResponseException) throwable)
                                        .getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR))
                .subscribe();
    }

    public Mono<MessagingSystemResponse> get(String path) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).build())
                .exchangeToMono(response -> Mono.just(new MessagingSystemResponse(response.statusCode().value())));
    }

    public Mono<MessagingSystemResponse> get(String path, String queryParamName, Object queryParamValue) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).queryParam(queryParamName, queryParamValue).build())
                .exchangeToMono(response -> Mono.just(new MessagingSystemResponse(response.statusCode().value())));
    }
}
