package com.example.impati.messaging_system_publisher.core;

import java.time.LocalDateTime;

public class SimplePublisher<T> implements Publisher<T> {

    private final ChannelRegistration channelRegistration;
    private final WebClientMessagingSystemClient client;

    public SimplePublisher(ChannelRegistration channelRegistration, WebClientMessagingSystemClient client) {
        this.channelRegistration = channelRegistration;
        this.client = client;
    }

    @Override
    public void publish(final T data) {
        Channel channel = channelRegistration.getChannel(data.getClass());
        client.post(
                "/v1/channels/" + channel.name() + "/messages-publication",
                new PublishRequest<>(LocalDateTime.now(), data));
    }

    record PublishRequest<T>(
            LocalDateTime createdAt,
            T data
    ) {

    }
}
