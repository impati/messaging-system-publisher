package com.example.impati.messaging_system_publisher.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleChannelProvider implements ChannelProvider {

    private Map<Class<? extends Object>, Channel> store = new HashMap<>();

    @Override
    public Channel getChannel(Class<? extends Object> clazz) {
        return store.get(clazz);
    }

    @Override
    public void addChannel(Channel channel, Class<? extends Object> clazz) {
        store.put(clazz, channel);
    }

    @Override
    public List<Channel> getAll() {
        return store.values().stream().toList();
    }
}
