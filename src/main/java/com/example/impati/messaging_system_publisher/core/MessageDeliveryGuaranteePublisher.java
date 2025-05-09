package com.example.impati.messaging_system_publisher.core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * 메시지 전달을 보장해주는 publisher 로써 내부적으로 FIFO 로 동작하는 queue 를 가지고 있다.
 */
public class MessageDeliveryGuaranteePublisher<T> implements Publisher<T> {

    private final ChannelRegistration channelRegistration;
    private final WebClient client;
    private final ChannelMessageRepository<T> channelMessageRepository;

    public MessageDeliveryGuaranteePublisher(
            ChannelRegistration channelRegistration,
            WebClient.Builder webClientBuilder,
            ChannelMessageRepository<T> channelMessageRepository,
            MessagingSystemPublisherProperties properties
    ) {
        this.channelRegistration = channelRegistration;
        this.client = webClientBuilder.baseUrl(properties.url()).build();
        this.channelMessageRepository = channelMessageRepository;
    }

    @Override
    public void publish(final T data) {
        Channel channel = channelRegistration.getChannel(data.getClass());
        String id = UUID.randomUUID().toString();
        Message<T> message = new Message<>(id, data, LocalDateTime.now());
        channelMessageRepository.insert(channel, message);
    }

    @Scheduled(fixedDelay = 600)
    public void workPublish() {
        for (Channel channel : channelRegistration.getChannels().values()) {
            Flux.fromIterable(channelMessageRepository.findMessagesByChannel(channel))
                    // flatMap 으로 Mono<Void> 내장 파이프라인을 펴고 동시에 최대 5개만 실행
                    .flatMap(message ->
                                    client.post()
                                            .uri("/v1/channels/{ch}/messages-publication", channel.name())
                                            .bodyValue(new PublishRequest<>(message.getId(), message.getCreatedAt(), message.getData()))
                                            .retrieve()
                                            .toBodilessEntity()
                                            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                                                    .filter(e -> e instanceof WebClientResponseException
                                                            && ((WebClientResponseException) e).getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR))
                                            .doOnSuccess(resp -> channelMessageRepository.pop(channel, message))
                                            .onErrorResume(e -> Mono.empty())
                                            .then()   // Mono<Void> 반환
                            , 5)
                    .subscribe();  // 이제 flatMap 내부 Mono들이 모두 구독되어 HTTP 요청이 실제로 실행됨
        }
    }
}
