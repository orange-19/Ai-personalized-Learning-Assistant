package com.personalizedlearningassistant.backend2.services.evaluate;

import com.personalizedlearningassistant.backend2.dto.pythonapis.EvaluateRequest;
import com.personalizedlearningassistant.backend2.dto.pythonapis.EvaluateResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EvaluateServices {

    private static String apiUrl = "http://localhost:5000/api/evaluate";

    public EvaluateResponse evaluateion(EvaluateRequest evaluateRequest) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EvaluateRequest> entity = new HttpEntity<>(evaluateRequest, headers);
        return rest.postForObject(apiUrl,entity, EvaluateResponse.class);
    }

}
