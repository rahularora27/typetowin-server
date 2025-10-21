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
        return getRandomQuote(wordCount, false, false);
    }
    
    public QuoteResponse getRandomQuote(int wordCount, boolean includePunctuation, boolean includeNumbers) {
        StringBuilder sentence = new StringBuilder();
        String[] punctuationMarks = {",", ".", "?", "!", ";", ":"};
        int wordsGenerated = 0;
        
        while (wordsGenerated < wordCount) {
            // Decide if this should be a number or a word
            boolean shouldAddNumber = includeNumbers && random.nextDouble() < 0.15; // 15% chance
            
            if (shouldAddNumber) {
                // Add a number as a separate word
                int number = random.nextInt(1000); // 0-999
                sentence.append(number);
            } else {
                // Add a regular word
                int randomIndex = random.nextInt(wordList.size());
                String word = wordList.get(randomIndex);
                sentence.append(word);
            }
            
            wordsGenerated++;
            
            // Add punctuation occasionally if enabled (not on last word)
            if (includePunctuation && wordsGenerated < wordCount && random.nextDouble() < 0.2) { // 20% chance
                String punctuation = punctuationMarks[random.nextInt(punctuationMarks.length)];
                sentence.append(punctuation);
            }
            
            // Add space if not the last word
            if (wordsGenerated < wordCount) {
                sentence.append(" ");
            }
        }
        
        // Always end with a period if punctuation is enabled
        if (includePunctuation) {
            sentence.append(".");
        }
        
        QuoteResponse quoteResponse = new QuoteResponse();
        quoteResponse.setText(sentence.toString());
        return quoteResponse;
    }
}
