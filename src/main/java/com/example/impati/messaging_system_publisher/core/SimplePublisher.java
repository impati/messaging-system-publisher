package com.example.impati.messaging_system_publisher.core;

import com.example.impati.messaging_system_publisher.config.Properties;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class SimplePublisher<T> implements Publisher<T> {

    private final WebClient client;
    private final Properties properties;
    private final SimpleChannelProvider channelProvider;

    public SimplePublisher(WebClient client, Properties properties, SimpleChannelProvider channelProvider) {
        this.client = client;
        this.properties = properties;
        this.channelProvider = channelProvider;
    }

    @Override
    public void publish(final T data) {
        Channel channel = channelProvider.getChannel(data.getClass());
        client.post()
                .uri(properties.url() + "/v1/channels/" + channel.name() + "/messages-publication")
                .bodyValue(data)
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
}
