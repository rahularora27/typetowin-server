package com.rahul.typetowin.application;

import com.rahul.typetowin.application.dto.QuoteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

    private final RestTemplate restTemplate;

    @Value("${quotable.api.url}")
    private String quotableApiUrl;

    public QuoteService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public QuoteResponse getRandomQuote() {
        try {
            return restTemplate.getForObject(quotableApiUrl, QuoteResponse.class);
        } catch (ResourceAccessException e) {
            logger.error("Error accessing Quotable API: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to retrieve quote from external API.", e);
        } catch (Exception e) {
            logger.error("Unexpected error while fetching quote: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error.", e);
        }
    }
}
