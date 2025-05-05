package com.example.impati.messaging_system_publisher.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MemoryChannelMessageRepository<T> implements ChannelMessageRepository<T> {

    private final Map<Channel, ConcurrentLinkedDeque<Message<T>>> stored = new HashMap<>();

    public MemoryChannelMessageRepository(ChannelRegistration channelRegistration) {
        channelRegistration.getChannels()
                .values()
                .forEach(channel -> stored.put(channel, new ConcurrentLinkedDeque<>()));
    }

    @Override
    public void insert(final Channel channel, final Message<T> message) {
        stored.get(channel).add(message);
    }

    @Override
    public void pop(final Channel channel, final Message<T> message) {
        stored.get(channel).remove(message);
    }

    @Override
    public List<Message<T>> findMessagesByChannel(final Channel channel) {
        return new ArrayList<>(stored.get(channel));
    }
}
