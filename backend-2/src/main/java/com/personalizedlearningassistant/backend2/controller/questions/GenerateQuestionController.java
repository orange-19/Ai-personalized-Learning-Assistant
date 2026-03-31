package com.personalizedlearningassistant.backend2.controller.questions;

import com.personalizedlearningassistant.backend2.dto.pythonapis.GenerateQuestionRequest;
import com.personalizedlearningassistant.backend2.dto.pythonapis.QuestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.personalizedlearningassistant.backend2.services.questions.GenerateQuestionServices;

@RestController
public class GenerateQuestionController {
    @Autowired
    private GenerateQuestionServices generateQuestionService;

    @PostMapping("/generate-questions")
    public ResponseEntity<QuestionResponse> generateQuestions(@RequestBody GenerateQuestionRequest request) {
        QuestionResponse response = generateQuestionService.generateQuestions(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
