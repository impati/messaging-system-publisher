package com.example.impati.messaging_system_publisher.core;

import java.time.LocalDateTime;
import java.util.UUID;

public class SimplePublisher<T> implements Publisher<T> {

    private final ChannelRegistration channelRegistration;
    private final MessagingSystemClient client;

    public SimplePublisher(ChannelRegistration channelRegistration, MessagingSystemClient client) {
        this.channelRegistration = channelRegistration;
        this.client = client;
    }

    @Override
    public void publish(final T data) {
        Channel channel = channelRegistration.getChannel(data.getClass());
        client.post(
                "/v1/channels/" + channel.name() + "/messages-publication",
                new PublishRequest<>(
                        UUID.randomUUID().toString().substring(0, 6),
                        LocalDateTime.now(),
                        data
                ));
    }
}
