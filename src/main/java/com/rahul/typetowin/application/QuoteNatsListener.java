package com.rahul.typetowin.application;

import com.fasterxml.jackson.databind.JsonNode;
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
            logger.info("Received initial quote request");
            try {
                QuoteResponse quoteResponse = quoteService.getRandomQuote();
                String jsonResponse = objectMapper.writeValueAsString(quoteResponse);
                natsConnection.publish(msg.getReplyTo(), jsonResponse.getBytes(StandardCharsets.UTF_8));
                logger.info("Replied with initial quote");
            } catch (Exception e) {
                logger.error("Error retrieving initial quote: {}", e.getMessage());
            }
        });

        dispatcher.subscribe("quote.request");
        logger.info("Listening for initial quote requests on 'quote.request'");

        Dispatcher nextDispatcher = natsConnection.createDispatcher((msg) -> {
            logger.info("Received next words request");
            try {
                int wordCount = 10;
                try {
                    JsonNode request = objectMapper.readTree(msg.getData());
                    if (request.has("wordCount")) {
                        wordCount = request.get("wordCount").asInt(10);
                    }
                } catch (Exception e) {
                    logger.warn("Could not parse word count from request, using default: {}", e.getMessage());
                }

                QuoteResponse quoteResponse = quoteService.getNextWords(wordCount);
                String jsonResponse = objectMapper.writeValueAsString(quoteResponse);
                natsConnection.publish(msg.getReplyTo(), jsonResponse.getBytes(StandardCharsets.UTF_8));
                logger.info("Replied with next words");
            } catch (Exception e) {
                logger.error("Error retrieving next words: {}", e.getMessage());
            }
        });

        nextDispatcher.subscribe("quote.next");
        logger.info("Listening for next words requests on 'quote.next'");
    }

    @PreDestroy
    public void destroy() {
        if (dispatcher != null) {
            dispatcher.unsubscribe("quote.request", 1);
        }
    }
}
