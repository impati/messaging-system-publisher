package com.example.impati.messaging_system_publisher.core;

import java.util.List;

public interface ChannelMessageRepository<T> {

    void insert(Channel channel, Message<T> message);

    void pop(Channel channel, Message<T> message);

    List<Message<T>> findMessagesByChannel(Channel channel);

}
