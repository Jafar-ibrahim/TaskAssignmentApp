package org.example.taskassignmentapp.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.example.taskassignmentapp.Enum.Role;
import org.example.taskassignmentapp.Model.User;
import org.example.taskassignmentapp.Model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserService {
    private final String BASE_URL = "http://host.docker.internal:8082/bootstrapper/users";
    private final TaskService taskService;
    @Autowired
    public UserService(TaskService taskService) {
        this.taskService = taskService;
    }

    public Optional<UserDTO> addUser(User user,
                                     String adminUsername, String adminPassword) {
        String url = BASE_URL + "/" + user.getUsername();

        HttpHeaders headers = new HttpHeaders();
        headers.add("password", user.getPassword());
        headers.add("role", user.getRole().toString());
        headers.add("adminUsername", adminUsername);
        headers.add("adminPassword", adminPassword);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("User added successfully with username: " + user.getUsername() + " and role: " + user.getRole());
                return Optional.of(new UserDTO(user.getUsername(), user.getRole()));
            } else {
                log.error("Failed to add user. Response: " + response.getBody());
                return Optional.empty();
            }
        } catch (HttpStatusCodeException e) {
            log.error("Failed to add user: with status code: " + e.getStatusCode() + " and message: " + e.getResponseBodyAsString());
            return Optional.empty();
        }
    }

    public boolean deleteUser(String username, String adminUsername, String adminPassword) {
        String url = BASE_URL + "/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.add("adminUsername", adminUsername);
        headers.add("adminPassword", adminPassword);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(url,HttpMethod.DELETE,requestEntity,String.class);
            taskService.deleteTasksByAssignee(username, adminUsername, adminPassword);
            log.info("User with username: " + username + " and their tasks have been deleted successfully");
            return true;
        } catch (HttpStatusCodeException e) {
            log.error("Failed to delete user: with status code: " + e.getStatusCode() + " and message: " + e.getResponseBodyAsString());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<UserDTO> getAllUsers(String adminUsername, String adminPassword) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("adminUsername", adminUsername);
        headers.set("adminPassword", adminPassword);

        HttpEntity<ObjectNode> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("Failed to create document: with status code: " + e.getStatusCode() + " and message: " + e.getResponseBodyAsString());
            return new ArrayList<>();
        }
    }

    public List<UserDTO> searchByUsername(String username, String adminUsername, String adminPassword){
        List<UserDTO> allUsers = getAllUsers(adminUsername, adminPassword);
        return allUsers.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.toLowerCase()))
                .collect(Collectors.toList());
    }

    public boolean userExists(String username) {
        String url = BASE_URL + "/" + username+"/node";
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpStatusCodeException e) {
            return false;
        }
    }
}
