package com.example.impati.messaging_system_publisher.config;

import com.example.impati.messaging_system_publisher.core.Channel;
import com.example.impati.messaging_system_publisher.core.ChannelProvider;
import com.example.impati.messaging_system_publisher.core.SimpleChannelProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ChannelBuilder {

    private final Map<Class<? extends Object>, Channel> store;

    public ChannelBuilder(Map<Class<? extends Object>, Channel> store) {
        this.store = store;
    }

    public ChannelBuilder addChannel(Channel channel, Class<? extends Object> clazz) {
        store.put(clazz, channel);
        return new ChannelBuilder(new HashMap<>(store));
    }

    public ChannelProvider build() {
        SimpleChannelProvider simpleChannelProvider = new SimpleChannelProvider();
        for (Entry<Class<? extends Object>, Channel> entry : store.entrySet()) {
            simpleChannelProvider.addChannel(entry.getValue(), entry.getKey());
        }

        return simpleChannelProvider;
    }
}
