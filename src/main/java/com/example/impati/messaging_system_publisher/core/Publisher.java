package com.example.impati.messaging_system_publisher.core;

public interface Publisher<T> {

    void publish(T data);
}
