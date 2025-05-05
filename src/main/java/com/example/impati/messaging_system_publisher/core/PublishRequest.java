package com.example.impati.messaging_system_publisher.core;

import java.time.LocalDateTime;

public record PublishRequest<T>(
        String id,
        LocalDateTime createdAt,
        T data
) {

}
