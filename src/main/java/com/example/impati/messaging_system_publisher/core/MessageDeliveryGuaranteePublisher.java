package com.example.impati.messaging_system_publisher.core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * 메시지 전달을 보장해주는 publisher 로써 내부적으로 FIFO 로 동작하는 queue 를 가지고 있다.
 */
public class MessageDeliveryGuaranteePublisher<T> implements Publisher<T> {

    private final ChannelRegistration channelRegistration;
    private final WebClient client;
    private final Map<Channel, Deque<T>> channelDequeMap = new HashMap<>();

    public MessageDeliveryGuaranteePublisher(
            ChannelRegistration channelRegistration,
            WebClient.Builder webClientBuilder
    ) {
        this.channelRegistration = channelRegistration;
        this.client = webClientBuilder.build();

    }

    @Override
    public void publish(final T data) {
        Channel channel = channelRegistration.getChannel(data.getClass());
        if (!channelDequeMap.containsKey(channel)) {
            channelDequeMap.put(channel, new ArrayDeque<>());
        }

        Deque<T> deque = channelDequeMap.get(channel);
        deque.addFirst(data);
    }

    @Scheduled(fixedDelay = 60)
    public void workPublish() {
        for (Entry<Channel, Deque<T>> entry : channelDequeMap.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }

            Channel channel = entry.getKey();
            Deque<T> deque = entry.getValue();

            while (!deque.isEmpty()) {
                post(channel, deque);
            }
        }
    }

    private void post(final Channel channel, final Deque<T> deque) {
        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/channels/" + channel.name() + "/messages-publication")
                        .build())
                .bodyValue(new PublishRequest<>(
                        UUID.randomUUID().toString().substring(0, 6),
                        LocalDateTime.now(),
                        deque.getLast()
                ))
                .retrieve()
                .toBodilessEntity()
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                        .filter(e -> e instanceof WebClientResponseException
                                && ((WebClientResponseException) e)
                                .getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR))
                .doOnSuccess(response -> deque.removeLast()) // 성공 시에만 실행
                .onErrorResume(e -> Mono.empty()) // 성공 시에만 실행
                .subscribe();
    }
}
