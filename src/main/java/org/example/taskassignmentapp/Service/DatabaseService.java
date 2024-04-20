package org.example.taskassignmentapp.Service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import org.example.taskassignmentapp.Model.Schema;
import org.example.taskassignmentapp.Model.Task;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Log4j2
@Service
public class DatabaseService {

    public static final String DATABASE_NAME = "TaskAssignmentApp";
    public static final String COLLECTION_NAME = "Tasks";
    public static final String BASE_URL = "http://host.docker.internal:9002/api/databases";
    private final RestTemplate restTemplate = new RestTemplate();

    public DatabaseService(){
        initializeDatabase();
        initializeCollection();
    }

    private void initializeDatabase() {
        if(!databaseExists()) {
            log.info("Initializing database: { {} }", DATABASE_NAME);
            if(createDatabase()){
                log.info("Database: { {} } initialized successfully", DATABASE_NAME);
            }else {
                log.error("Failed to initialize database: { {} }", DATABASE_NAME);
            }
        }else {
            log.info("Database: { {} } already exists and is ready for use.", DATABASE_NAME);
        }
    }

    private void initializeCollection() {
        if(!collectionExists()) {
            log.info("Initializing collection: { {} }", COLLECTION_NAME);
            try {
                if(createCollection()){
                    log.info("Collection: { {} } initialized successfully", COLLECTION_NAME);
                }else {
                    log.error("Failed to initialize collection: { {} }", COLLECTION_NAME);
                }
            } catch (IOException e) {
                log.error("Failed to create collection: { {} } with error: { {} }", COLLECTION_NAME, e.getMessage());
            }
        }else {
            log.info("Collection: { {} } already exists and is ready for use.", COLLECTION_NAME);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean collectionExists(){
        String url = BASE_URL +"/"+ DATABASE_NAME + "/collections";
        try {
            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, createHttpEntityWithAuthHeaders(), List.class);
            List<String> list = response.getBody();
            return list != null && list.contains(COLLECTION_NAME);
        }catch (HttpStatusCodeException e){
            log.error("Failed to check existence of Collection { {} } with status code: { {} } and message: { {} }", COLLECTION_NAME, e.getStatusCode(), e.getResponseBodyAsString());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean databaseExists(){
        try {
            ResponseEntity<List> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, createHttpEntityWithAuthHeaders(), List.class);
            List<String> list = response.getBody();
            return list != null && list.contains(DATABASE_NAME);
        }catch (HttpStatusCodeException e){
            log.error("Failed to check existence of database { {} } with status code: { {} } and message: { {} }", DATABASE_NAME, e.getStatusCode(), e.getResponseBodyAsString());
            return false;
        }
    }

    private boolean createDatabase(){
        String url = BASE_URL+"/"+ DATABASE_NAME;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, createHttpEntityWithAuthHeaders(), String.class);
            return response.getStatusCode().is2xxSuccessful();
        }catch (HttpStatusCodeException e){
            log.error("Failed to create database: { {} } with status code: { {} } and message: { {} }", url, e.getStatusCode(), e.getResponseBodyAsString());
            return false;
        }
    }

    private boolean createCollection() throws IOException {
        JsonNode schema = Schema.fromClass(Task.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("username", "admin");
        headers.add("password", "admin");
        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(schema,headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(getCollectionUrl(), HttpMethod.POST, requestEntity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        }catch (HttpStatusCodeException e){
            log.error("Failed to create collection: { {} } with status code: { {} } and message: { {} }", getCollectionUrl(), e.getStatusCode(), e.getResponseBodyAsString());
            return false;
        }
    }

    private HttpEntity<String> createHttpEntityWithAuthHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("username", "admin");
        headers.add("password", "admin");
        return new HttpEntity<>(headers);
    }

    public String getCollectionUrl(){
        return BASE_URL+"/"+ DATABASE_NAME + "/collections/" + COLLECTION_NAME;
    }
}