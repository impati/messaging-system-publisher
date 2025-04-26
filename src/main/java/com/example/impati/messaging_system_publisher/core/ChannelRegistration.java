package com.example.impati.messaging_system_publisher.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChannelRegistration {

    private final Map<Class<?>, Channel> channels;

    private ChannelRegistration(ChannelRegistrationBuilder channelRegistrationBuilder) {
        this.channels = Collections.unmodifiableMap(channelRegistrationBuilder.channels);
    }

    public static ChannelRegistrationBuilder builder() {
        return new ChannelRegistrationBuilder();
    }

    public Map<Class<?>, Channel> getChannels() {
        return channels;
    }

    public Channel getChannel(Class<?> clazz) {
        return channels.get(clazz);
    }

    public static class ChannelRegistrationBuilder {

        private final Map<Class<?>, Channel> channels = new LinkedHashMap<>();

        /**
         * 채널과 핸들러 클래스를 매핑하여 등록합니다.
         *
         * @param channel      등록할 채널
         * @param handlerClass 해당 채널의 핸들러 클래스
         * @param <T>          핸들러 타입
         * @return 빌더 자신
         */
        public <T> ChannelRegistrationBuilder addChannel(Channel channel, Class<T> handlerClass) {
            channels.put(handlerClass, channel);
            return this;
        }

        /**
         * 빌더를 통해 구성된 ChannelRegistration 객체를 생성합니다.
         */
        public ChannelRegistration build() {
            return new ChannelRegistration(this);
        }
    }
}
