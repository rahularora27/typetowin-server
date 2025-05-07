package com.rahul.typetowin.application;

import com.rahul.typetowin.application.dto.QuoteResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class QuoteService {
    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);
    private final List<String> wordList = new ArrayList<>();
    private final Random random = new Random();
    private final ResourceLoader resourceLoader;

    public QuoteService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadWordList() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resourceLoader.getResource("classpath:words.txt").getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordList.add(line.trim().toLowerCase());
            }
            logger.info("Loaded {} words from words.txt", wordList.size());
        } catch (Exception e) {
            logger.error("Error loading word list: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to load word list", e);
        }
    }

    public QuoteResponse getRandomQuote(int wordCount) {
        StringBuilder sentence = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            int randomIndex = random.nextInt(wordList.size());
            sentence.append(wordList.get(randomIndex)).append(" ");
        }
        QuoteResponse quoteResponse = new QuoteResponse();
        quoteResponse.setText(sentence.toString().trim());
        return quoteResponse;
    }
}
