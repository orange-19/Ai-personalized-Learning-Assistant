package com.personalizedlearningassistant.backend2.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class HomeController {

    @GetMapping("/home")
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("Welcome to the Personalized Learning Assistant API!", HttpStatus.OK);
    }

}

