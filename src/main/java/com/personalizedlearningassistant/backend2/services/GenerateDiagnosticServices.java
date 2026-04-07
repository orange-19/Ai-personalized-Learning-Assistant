package com.personalizedlearningassistant.backend2.services;


import com.personalizedlearningassistant.backend2.dto.pythonapis.diagnosticmodule.GenerateDiagnosticRequest;
import com.personalizedlearningassistant.backend2.dto.pythonapis.diagnosticmodule.GenerateDiagnosticResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GenerateDiagnosticServices {

    private static String apiUrl = "http://localhost:5000/api/generate-diagnostic";

    public GenerateDiagnosticResponse generateDiagnostic(GenerateDiagnosticRequest generateDiagnosticRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<GenerateDiagnosticRequest> entity = new HttpEntity<>(generateDiagnosticRequest, headers);
        return restTemplate.postForObject(apiUrl, entity, GenerateDiagnosticResponse.class);
    }
}
