package org.example.taskassignmentapp.Service;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.example.taskassignmentapp.Enum.Role;
import org.example.taskassignmentapp.Model.UserDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
public class AuthenticationService {

    public Optional<UserDTO> authenticateUser(String username, String password){
        int userNode = getUserNode(username);
        if (userNode == -1) {
            return Optional.empty();
        }
        String url = "http://node"+userNode+":9000/api/auth?username="+username+"&password="+password;
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Role role = Role.valueOf(Objects.requireNonNull(response.getBody()).get("role").toString());
                return Optional.of(new UserDTO(username, role));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Login failed: " + e.getMessage());
            return Optional.empty();
        }
    }
    public static boolean hasRole(HttpSession httpSession, Role role) {
        UserDTO user = (UserDTO) httpSession.getAttribute("user");
        return user != null && user.getRole() == role;
    }
    public int getUserNode(String username){
        String url =  "http://host.docker.internal:8082/bootstrapper/users/"+username+"/node";
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if(Objects.requireNonNull(response.getBody()).matches("\\d+")) {
                return Integer.parseInt(response.getBody());
            } else {
                log.error("Error: " + response.getBody());
                return -1;
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            return -1;
        }
    }


}
