package com.example.impati.messaging_system_publisher.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

public class WebClientConfig {

    public WebClient webClient() {
        // 커넥션 풀 설정
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom-connection-pool")
                .maxConnections(50)              // 최대 50개 커넥션
                .pendingAcquireMaxCount(100)     // 커넥션 획득 대기 큐 사이즈
                .build();

        // HTTP 클라이언트 설정
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)   // 연결 시도 타임아웃 1초
                .followRedirect(true)                                 // 리다이렉트 자동 추적
                .responseTimeout(Duration.ofSeconds(1))               // 응답 전체 타임아웃 1초
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(1, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(1, TimeUnit.SECONDS))
                );

        // WebClient 빈 생성
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
