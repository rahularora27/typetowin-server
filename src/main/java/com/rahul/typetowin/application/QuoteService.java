package com.rahul.typetowin.application;

import com.rahul.typetowin.application.dto.QuoteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

    private final RestTemplate restTemplate;

    private final List<String> wordList = new ArrayList<>();
    private final Random random = new Random();
    private final ResourceLoader resourceLoader;

    private Resource wordsFile;

    public QuoteService(RestTemplateBuilder restTemplateBuilder, ResourceLoader resourceLoader) {
        this.restTemplate = restTemplateBuilder.build();
        this.resourceLoader = resourceLoader;
        this.wordsFile = resourceLoader.getResource("classpath:words.txt");
        loadWordList();
    }

    private void loadWordList() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(wordsFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordList.add(line.trim().toLowerCase());
            }
            logger.info("Loaded {} words from words.txt", wordList.size());
        } catch (IOException e) {
            logger.error("Error loading word list: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to load word list", e);
        }
    }

    private String generateRandomWords(int wordCount) {
        StringBuilder sentence = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            int randomIndex = random.nextInt(wordList.size());
            sentence.append(wordList.get(randomIndex)).append(" ");
        }
        return sentence.toString().trim();
    }

    public QuoteResponse getRandomQuote() {
        try {
            String randomSentence = generateRandomWords(20);
            QuoteResponse quoteResponse = new QuoteResponse();
            quoteResponse.setText(randomSentence);
            return quoteResponse;
        } catch (Exception e) {
            logger.error("Error generating random quote: {}", e.getMessage(), e);
            throw new IllegalStateException("Error generating random quote", e);
        }
    }

    public QuoteResponse getNextWords(int wordCount) {
        try {
            String randomWords = generateRandomWords(wordCount);
            QuoteResponse quoteResponse = new QuoteResponse();
            quoteResponse.setText(randomWords);
            return quoteResponse;
        } catch (Exception e) {
            logger.error("Error generating next words: {}", e.getMessage(), e);
            throw new IllegalStateException("Error generating next words", e);
        }
    }
}
