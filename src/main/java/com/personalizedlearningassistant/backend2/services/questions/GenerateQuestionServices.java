package com.personalizedlearningassistant.backend2.services.questions;

import com.personalizedlearningassistant.backend2.dto.pythonapis.GenerateQuestionRequest;
import com.personalizedlearningassistant.backend2.dto.pythonapis.QuestionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class GenerateQuestionServices {

    private static final String EXTERNAL_URL = "http://0.0.0.0:5000/api/generate-questions";

    /**
     * Sends the given GenerateQuestionRequest to the external question-generation API
     * and returns the raw JSON response body wrapped in a ResponseEntity (status preserved).
     *
     * Contract:
     * - input: non-null GenerateQuestionRequest
     * - output: ResponseEntity<String> containing the JSON body from the external service
     * - error modes: returns 503 Service Unavailable with a small JSON error body when the external service can't be reached
     */
    public QuestionResponse generateQuestions(GenerateQuestionRequest requestBody) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GenerateQuestionRequest> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(EXTERNAL_URL, entity, QuestionResponse.class);
    }
}
