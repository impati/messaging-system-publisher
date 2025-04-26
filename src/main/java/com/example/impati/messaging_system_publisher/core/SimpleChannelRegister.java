package com.example.impati.messaging_system_publisher.core;

import java.util.List;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

public class SimpleChannelRegister implements ChannelRegister {

    private final MessagingSystemClient client;

    public SimpleChannelRegister(MessagingSystemClient client) {
        this.client = client;
    }

    public void register(List<Channel> channels) {

        for (Channel channel : channels) {
            client.get("/v1/channels/" + channel.name())
                    .flatMap(response -> {
                        if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
                            client.post("/v1/channels", new ChannelRequest(channel.name()));
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
