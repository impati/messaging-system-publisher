package com.example.impati.messaging_system_publisher.core;

import java.util.List;

public interface ChannelProvider {

    Channel getChannel(Class<? extends Object> clazz);

    void addChannel(Channel channel, Class<? extends Object> clazz);

    List<Channel> getAll();
}
