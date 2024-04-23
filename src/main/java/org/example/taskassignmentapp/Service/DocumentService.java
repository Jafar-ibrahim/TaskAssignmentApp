package org.example.taskassignmentapp.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
public class DocumentService {

    private final DatabaseService databaseService;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    @Autowired
    public DocumentService(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    private ResponseEntity<String> makeRequest(String url, HttpMethod method, ObjectNode body, String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", username);
        headers.set("password", password);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ObjectNode> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, method, entity, String.class);
    }

    public String createDocument(ObjectNode documentJson, String username, String password) {
        String url = databaseService.getCollectionUrl() + "/documents";
        try {
            ResponseEntity<String> response = makeRequest(url, HttpMethod.POST, documentJson, username, password);
            log.info("Document created successfully");
            return extractDocumentId(response.getBody());
        } catch (HttpStatusCodeException e) {
            log.error("Failed to create document: with status code: " + e.getStatusCode() + " and message: " + e.getResponseBodyAsString());
            return null;
        }
    }

    public void deleteDocument(String documentId, String username, String password) {
        String url = databaseService.getCollectionUrl() + "/documents/" + documentId;
        try {
            makeRequest(url, HttpMethod.DELETE, null, username, password);
            log.info("Document deleted successfully");
        } catch (HttpStatusCodeException e) {
            log.error("Failed to delete document: with status code: " + e.getStatusCode() + " and message: " + e.getResponseBodyAsString());
        }
    }

    public void updateDocument(String documentId, ObjectNode updatedProperties, String username, String password) {
        String url = databaseService.getCollectionUrl() + "/documents/" + documentId;
        try {
            makeRequest(url, HttpMethod.PUT, updatedProperties, username, password);
            log.info("Document updated successfully");
        } catch (HttpStatusCodeException e) {
            log.error("Failed to update document: with status code: " + e.getStatusCode() + " and message: " + e.getResponseBodyAsString());
        }
    }

    public Optional<ObjectNode> getDocumentById(String documentId, String username, String password) {
        String url = databaseService.getCollectionUrl() + "/documents/" + documentId;
        try {
            ResponseEntity<String> response = makeRequest(url, HttpMethod.GET, null, username, password);
            ObjectNode document = mapper.readValue(response.getBody(), ObjectNode.class);
            log.info("Fetched document by id successfully");
            return Optional.of(document);
        } catch (Exception e) {
            log.error("Failed to fetch document by id: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<JsonNode> fetchAllDocumentsByPropertyValue(String propertyName, String propertyValue, String username, String password) {
        String url = databaseService.getCollectionUrl() + "/documents?property_name=" + propertyName + "&property_value=\"" + propertyValue+"\"";
        try {
            ResponseEntity<String> response = makeRequest(url, HttpMethod.GET, null, username, password);
            List<JsonNode> documents = mapper.readValue(response.getBody(), mapper.getTypeFactory().constructCollectionType(List.class, JsonNode.class));
            log.info("Fetched all documents by property : " + propertyName + " value: " + propertyValue);
            return documents;
        } catch (Exception e) {
            log.error("Failed to fetch all documents by property : " + propertyName + " value: " + propertyValue + " with error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String extractDocumentId(String response) {
        String patternString = "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }
}