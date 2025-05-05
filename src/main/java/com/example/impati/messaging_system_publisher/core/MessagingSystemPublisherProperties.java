package com.example.impati.messaging_system_publisher.core;

public record MessagingSystemPublisherProperties(
        String url,
        String clientName,
        boolean deliveryGuarantee
) {

}
