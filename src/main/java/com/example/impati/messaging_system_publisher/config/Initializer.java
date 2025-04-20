package com.example.impati.messaging_system_publisher.config;

import com.example.impati.messaging_system_publisher.core.Channel;
import com.example.impati.messaging_system_publisher.core.ChannelProvider;
import java.time.Duration;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class Initializer {

    private final WebClient client;
    private final Properties properties;
    private final ChannelBuilder channelBuilder;

    public Initializer(WebClient client, Properties properties, ChannelBuilder channelBuilder) {
        this.client = client;
        this.properties = properties;
        this.channelBuilder = channelBuilder;
    }

    public void initialize() {
        // 클라이언트 등록
        client.post()
                .uri(properties.url() + "/v1/client?clientName=" + properties.clientName())
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> Mono.empty())
                .retryWhen(
                        Retry.fixedDelay(3, Duration.ofSeconds(1))
                                .filter(throwable -> throwable instanceof WebClientResponseException
                                        && ((WebClientResponseException) throwable)
                                        .getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR))
                .subscribe();

        // 채널 생성
        ChannelProvider channelProvider = channelBuilder.build();
        List<Channel> channels = channelProvider.getAll();
        for (Channel channel : channels) {
            client.get()
                    .uri(properties.url() + "/v1/channels/" + channel.name())
                    .exchangeToMono(response -> {
                        if (response.statusCode() == HttpStatus.NOT_FOUND) {
                            return client.post()
                                    .uri(properties.url() + "/channels")
                                    .bodyValue(new ChannelRequest(channel.name()))
                                    .retrieve()
                                    .bodyToMono(Void.class);
                        }
                        return Mono.empty();
                    })
                    .subscribe();
        }
    }

    record ChannelRequest(
            String channelName
    ) {

    }
}
