package com.example.impati.messaging_system_publisher.core;

import com.example.impati.messaging_system_publisher.config.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ChannelProviderTest {

    @Test
    public void test() {
        SimpleChannelProvider channelForwarder = new SimpleChannelProvider();
        channelForwarder.addChannel(new Channel("properties"), Properties.class);

        Channel channel = channelForwarder.getChannel(Properties.class);

        assertThat(channel.name()).isEqualTo("properties");
    }
}
