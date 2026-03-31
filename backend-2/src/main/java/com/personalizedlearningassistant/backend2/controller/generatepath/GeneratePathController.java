package com.personalizedlearningassistant.backend2.controller.generatepath;

import com.personalizedlearningassistant.backend2.dto.pythonapis.generatepath.GeneratePathRequest;
import com.personalizedlearningassistant.backend2.dto.pythonapis.generatepath.GeneratePathResponse;
import com.personalizedlearningassistant.backend2.services.generatepath.GeneratePathServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class GeneratePathController {
    private static final Logger logger = LoggerFactory.getLogger(GeneratePathController.class);

    @Autowired
    private GeneratePathServices generatePathServices;

    @PostMapping("/generate-path")
    public ResponseEntity<?> genPath(@RequestBody GeneratePathRequest request){
        try {
            if (request == null || request.getUsername() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing username in request");
            }

            GeneratePathResponse response = generatePathServices.genPath(request);
            if (response == null) {
                logger.error("GeneratePathService returned null for username {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate learning path");
            }

            boolean saved = generatePathServices.savePath(response, request.getUsername());
            if (!saved) {
                logger.warn("Generated path for {} but failed to save to database", request.getUsername());
                // still return the generated response but indicate saving failed
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Generated path but failed to save");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error while generating path for username {}", request != null ? request.getUsername() : "<null>", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
