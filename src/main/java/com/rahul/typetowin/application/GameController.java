package com.rahul.typetowin.application;

import com.rahul.typetowin.application.dto.GameSession;
import com.rahul.typetowin.application.dto.QuoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private QuoteService quoteService;

    // 1. Get a random quote
    @GetMapping("/next")
    public ResponseEntity<QuoteResponse> getNextQuote(@RequestParam(value = "wordCount", defaultValue = "10") int wordCount) {
        QuoteResponse quote = quoteService.getRandomQuote(wordCount);
        return ResponseEntity.ok(quote);
    }

    // 2. Create a new game session
    @PostMapping("/session")
    public ResponseEntity<GameSession> createSession() {
        String sessionId = UUID.randomUUID().toString();
        QuoteResponse quote = quoteService.getRandomQuote(20); // or any default word count

        GameSession session = new GameSession();
        session.setSessionId(sessionId);
        session.setQuote(quote.getText());

        return ResponseEntity.ok(session);
    }
}
