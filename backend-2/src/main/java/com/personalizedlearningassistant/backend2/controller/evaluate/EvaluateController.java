package com.personalizedlearningassistant.backend2.controller.evaluate;

import com.personalizedlearningassistant.backend2.dto.pythonapis.EvaluateRequest;
import com.personalizedlearningassistant.backend2.dto.pythonapis.EvaluateResponse;
import com.personalizedlearningassistant.backend2.services.evaluate.EvaluateServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EvaluateController {
    @Autowired
    private EvaluateServices evaluateServices;

    @PostMapping("/evaluate")
    public ResponseEntity<EvaluateResponse> evaluating(@RequestBody EvaluateRequest evaluateRequest) {
        EvaluateResponse response = evaluateServices.evaluateion(evaluateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
