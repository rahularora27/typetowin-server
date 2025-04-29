package com.rahul.typetowin.application;

import com.rahul.typetowin.application.dto.GameResult;
import com.rahul.typetowin.application.dto.GameSession;
import com.rahul.typetowin.application.dto.QuoteResponse;
import com.rahul.typetowin.application.entity.GameResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private NatsQuoteClient natsQuoteClient;

    @Autowired
    private GameResultRepository gameResultRepository;

    // 1. Get a random quote
    @GetMapping("/next")
    public ResponseEntity<QuoteResponse> getNextQuote(@RequestParam(value = "wordCount", defaultValue = "10") int wordCount) {
        String quoteText = natsQuoteClient.requestQuote(wordCount);
        QuoteResponse quote = new QuoteResponse();
        quote.setText(quoteText);
        return ResponseEntity.ok(quote);
    }

    // 2. Create a new game session
    @PostMapping("/session")
    public ResponseEntity<GameSession> createSession() {
        String sessionId = UUID.randomUUID().toString();
        String quoteText = natsQuoteClient.requestQuote(20); // or any default word count

        GameSession session = new GameSession();
        session.setSessionId(sessionId);
        session.setQuote(quoteText);

        return ResponseEntity.ok(session);
    }

    // 3. Submit game result
    @PostMapping("/result")
    public ResponseEntity<?> submitResult(@RequestBody GameResult result) {
        GameResultEntity entity = new GameResultEntity();
        entity.setSessionId(result.getSessionId());
        entity.setCorrectChars(result.getCorrectChars());
        entity.setIncorrectChars(result.getIncorrectChars());
        entity.setTimer(result.getTimer());
        gameResultRepository.save(entity);
        return ResponseEntity.ok().build();
    }
}

