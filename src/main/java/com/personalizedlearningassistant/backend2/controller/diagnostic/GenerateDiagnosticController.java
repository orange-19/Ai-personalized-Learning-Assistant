package com.personalizedlearningassistant.backend2.controller.diagnostic;

import com.personalizedlearningassistant.backend2.dto.pythonapis.diagnosticmodule.GenerateDiagnosticRequest;
import com.personalizedlearningassistant.backend2.dto.pythonapis.diagnosticmodule.GenerateDiagnosticResponse;
import com.personalizedlearningassistant.backend2.services.GenerateDiagnosticServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class GenerateDiagnosticController {

    @Autowired
    private GenerateDiagnosticServices generateDiagnosticServices;

    @PostMapping("/generate-diagnostic")
    public ResponseEntity<GenerateDiagnosticResponse> generateDiagnostic(@RequestBody GenerateDiagnosticRequest generateDiagnosticRequest) {
        GenerateDiagnosticResponse response = generateDiagnosticServices.generateDiagnostic(generateDiagnosticRequest);
        System.out.println(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
