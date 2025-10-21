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
    public ResponseEntity<QuoteResponse> getNextQuote(
            @RequestParam(value = "wordCount", defaultValue = "10") int wordCount,
            @RequestParam(value = "punctuation", defaultValue = "false") boolean includePunctuation,
            @RequestParam(value = "numbers", defaultValue = "false") boolean includeNumbers) {
        QuoteResponse quote = quoteService.getRandomQuote(wordCount, includePunctuation, includeNumbers);
        return ResponseEntity.ok(quote);
    }

    // 2. Create a new game session
    @PostMapping("/session")
    public ResponseEntity<GameSession> createSession(
            @RequestParam(value = "punctuation", defaultValue = "false") boolean includePunctuation,
            @RequestParam(value = "numbers", defaultValue = "false") boolean includeNumbers) {
        String sessionId = UUID.randomUUID().toString();
        QuoteResponse quote = quoteService.getRandomQuote(20, includePunctuation, includeNumbers);

        GameSession session = new GameSession();
        session.setSessionId(sessionId);
        session.setQuote(quote.getText());

        return ResponseEntity.ok(session);
    }
}
