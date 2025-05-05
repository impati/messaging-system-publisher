package com.example.impati.messaging_system_publisher.core;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message<T> {

    private final String id;
    private final T data;
    private final LocalDateTime createdAt;

    public Message(String id, T data, LocalDateTime createdAt) {
        this.id = id;
        this.data = data;
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message<?> message = (Message<?>) o;
        return Objects.equals(id, message.id);
    }
}
