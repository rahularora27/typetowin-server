package com.rahul.typetowin.application;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class NatsQuoteClient {

    private final Connection natsConnection;

    public NatsQuoteClient(Connection natsConnection) {
        this.natsConnection = natsConnection;
    }

    public String requestQuote(int wordCount) {
        try {
            String requestPayload = String.valueOf(wordCount);
            Message reply = natsConnection.request("quote.request", requestPayload.getBytes(StandardCharsets.UTF_8), Duration.ofSeconds(2));
            return new String(reply.getData(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get quote from NATS", e);
        }
    }
}
