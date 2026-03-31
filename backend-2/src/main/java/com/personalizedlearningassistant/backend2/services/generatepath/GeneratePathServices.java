package com.personalizedlearningassistant.backend2.services.generatepath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personalizedlearningassistant.backend2.dto.pythonapis.generatepath.GeneratePathRequest;
import com.personalizedlearningassistant.backend2.dto.pythonapis.generatepath.GeneratePathResponse;
import com.personalizedlearningassistant.backend2.model.LearningPathEntity;
import com.personalizedlearningassistant.backend2.model.UserProfile;
import com.personalizedlearningassistant.backend2.repository.LearningPathRepository;
import com.personalizedlearningassistant.backend2.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class GeneratePathServices {

    private static final Logger log = LoggerFactory.getLogger(GeneratePathServices.class);

    private final LearningPathRepository learningPathRepository;
    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Injected from application.properties: python.api.generate-path.url=http://localhost:5000/api/generate-path
    @Value("${python.api.generate-path.url}")
    private String apiUrl;

    // Constructor injection (preferred over @Autowired on fields)
    public GeneratePathServices(LearningPathRepository learningPathRepository,
                                ProfileRepository profileRepository,
                                RestTemplate restTemplate,
                                ObjectMapper objectMapper) {
        this.learningPathRepository = learningPathRepository;
        this.profileRepository = profileRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Calls the Python microservice to generate a learning path.
     *
     * @param request the generation request payload
     * @return the response from the Python API, or null if the call fails
     */
    public GeneratePathResponse genPath(GeneratePathRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GeneratePathRequest> entity = new HttpEntity<>(request, headers);

            GeneratePathResponse response = restTemplate.postForObject(apiUrl, entity, GeneratePathResponse.class);
            log.info("Successfully received response from generate-path API");
            return response;

        } catch (RestClientException ex) {
            log.error("Failed to call generate-path API at {}: {}", apiUrl, ex.getMessage());
            return null;
        }
    }

    /**
     * Persists a GeneratePathResponse as a LearningPathEntity linked to the given user.
     *
     * Mapping summary:
     *  - response.isSuccess()        → entity.success (boolean)
     *  - response.getLearningpath()  → entity.learningpathJson (serialized to JSON string)
     *  - response.getDays()          → entity.daysJson (serialized to JSON string)
     *  - username lookup             → entity.userProfile (@ManyToOne FK: user_profile_id)
     *
     * @param response the API response to persist
     * @param username the username to associate this learning path with
     * @return true if saved successfully, false otherwise
     */
    public boolean savePath(GeneratePathResponse response, String username) {
        if (response == null) {
            log.warn("savePath called with null response for user '{}'", username);
            return false;
        }

        // 1. Resolve the UserProfile — required for the FK relationship
        UserProfile user = profileRepository.findByUsername(username);
        if (user == null) {
            log.warn("No user found with username '{}', cannot save learning path", username);
            return false;
        }

        try {
            // 2. Serialize nested objects to JSON strings for @Lob TEXT storage
            String learningpathJson = objectMapper.writeValueAsString(response.getLearningpath());
            String daysJson = objectMapper.writeValueAsString(response.getDays());

            // 3. Build entity — JPA will store user.id as the user_profile_id FK column
            LearningPathEntity entity = new LearningPathEntity(
                    response.isSuccess(),
                    learningpathJson,
                    daysJson,
                    user
            );

            learningPathRepository.save(entity);

            System.out.println(entity.toString());
            log.info("Learning path saved for user '{}' with id '{}'", username, entity.getId());
            return true;

        } catch (JsonProcessingException jpe) {
            log.error("JSON serialization failed while saving learning path for user '{}': {}", username, jpe.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error saving learning path for user '{}': {}", username, e.getMessage());
            return false;
        }
    }
}