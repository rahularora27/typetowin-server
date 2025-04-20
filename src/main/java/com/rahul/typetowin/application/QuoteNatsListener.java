package com.rahul.typetowin.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.typetowin.application.dto.QuoteResponse;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class QuoteNatsListener {

    private static final Logger logger = LoggerFactory.getLogger(QuoteNatsListener.class);

    @Autowired
    private Connection natsConnection;

    @Autowired
    private QuoteService quoteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Dispatcher dispatcher;

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        dispatcher = natsConnection.createDispatcher((msg) -> {
            logger.info("Received quote request on subject: {}", msg.getSubject());
            try {
                QuoteResponse quoteResponse = quoteService.getRandomQuote();
                String jsonResponse = objectMapper.writeValueAsString(quoteResponse);
                natsConnection.publish(msg.getReplyTo(), jsonResponse.getBytes(StandardCharsets.UTF_8));
                logger.info("Replied with quote to subject: {}", msg.getReplyTo());
            } catch (Exception e) {
                logger.error("Error retrieving quote: {}", e.getMessage());
            }
        });

        dispatcher.subscribe("quote.request");
        logger.info("Listening for quote requests on 'quote.request'");
    }

    @PreDestroy
    public void destroy() {
        if (dispatcher != null) {
            dispatcher.unsubscribe("quote.request", 1);
        }
    }
}
