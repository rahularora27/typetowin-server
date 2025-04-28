package com.rahul.typetowin.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.typetowin.application.dto.GameResult;
import com.rahul.typetowin.application.dto.GameSession;
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
import java.util.Map;
import java.util.UUID;

@Component
public class QuoteNatsListener {

    private static final Logger logger = LoggerFactory.getLogger(QuoteNatsListener.class);

    @Autowired
    private Connection natsConnection;

    @Autowired
    private QuoteService quoteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Dispatcher quoteNextDispatcher;
    private Dispatcher sessionDispatcher;
    private Dispatcher resultDispatcher;

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        // --- quote.next handler ---
        quoteNextDispatcher = natsConnection.createDispatcher((msg) -> {
            logger.info("Received quote.next request");
            try {
                // Parse wordCount from request if present
                int wordCount = 10; // default
                try {
                    Map<String, Object> req = objectMapper.readValue(msg.getData(), Map.class);
                    if (req.containsKey("wordCount")) {
                        wordCount = (int) req.get("wordCount");
                    }
                } catch (Exception e) {
                    logger.warn("Could not parse wordCount from quote.next request, using default 10");
                }

                // Fetch the quote (implement getRandomWords in your service if needed)
                QuoteResponse quoteResponse = quoteService.getRandomQuote(); // or getRandomWords(wordCount)
                String jsonResponse = objectMapper.writeValueAsString(quoteResponse);
                natsConnection.publish(msg.getReplyTo(), jsonResponse.getBytes(StandardCharsets.UTF_8));
                logger.info("Replied with new quote for quote.next");
            } catch (Exception e) {
                logger.error("Error handling quote.next: {}", e.getMessage());
            }
        });
        quoteNextDispatcher.subscribe("quote.next");
        logger.info("Listening for quote.next requests on 'quote.next'");

        // --- game.session.create handler ---
        sessionDispatcher = natsConnection.createDispatcher((msg) -> {
            logger.info("Received game session creation request");
            try {
                String sessionId = UUID.randomUUID().toString();
                QuoteResponse quoteResponse = quoteService.getRandomQuote();

                GameSession session = new GameSession();
                session.setSessionId(sessionId);
                session.setQuote(quoteResponse.getText());

                String jsonResponse = objectMapper.writeValueAsString(session);
                natsConnection.publish(msg.getReplyTo(), jsonResponse.getBytes(StandardCharsets.UTF_8));
                logger.info("Replied with new game session");
            } catch (Exception e) {
                logger.error("Error creating game session: {}", e.getMessage());
            }
        });
        sessionDispatcher.subscribe("game.session.create");
        logger.info("Listening for game session creation on 'game.session.create'");

        // --- game.session.result handler ---
        resultDispatcher = natsConnection.createDispatcher((msg) -> {
            logger.info("Received game result submission");
            try {
                GameResult result = objectMapper.readValue(msg.getData(), GameResult.class);
                // Here you can store the result in a database or log it
                logger.info("Game result: sessionId={}, correct={}, incorrect={}, timer={}",
                        result.getSessionId(), result.getCorrectChars(), result.getIncorrectChars(), result.getTimer());
                natsConnection.publish(msg.getReplyTo(), "OK".getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                logger.error("Error processing game result: {}", e.getMessage());
            }
        });
        resultDispatcher.subscribe("game.session.result");
        logger.info("Listening for game result submissions on 'game.session.result'");
    }

    @PreDestroy
    public void destroy() {
        if (quoteNextDispatcher != null) {
            quoteNextDispatcher.unsubscribe("quote.next");
        }
        if (sessionDispatcher != null) {
            sessionDispatcher.unsubscribe("game.session.create");
        }
        if (resultDispatcher != null) {
            resultDispatcher.unsubscribe("game.session.result");
        }
    }
}
