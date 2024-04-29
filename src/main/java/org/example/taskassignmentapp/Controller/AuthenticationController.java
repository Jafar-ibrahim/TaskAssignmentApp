package org.example.taskassignmentapp.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.taskassignmentapp.Enum.Role;
import org.example.taskassignmentapp.Model.UserDTO;
import org.example.taskassignmentapp.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/task-app/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model,
                        HttpSession httpSession) {

        Optional<UserDTO> userDTO = authenticationService.authenticateUser(username, password);
        if (userDTO.isPresent()) {
            UserDTO user = userDTO.get();
            httpSession.setAttribute("user", user);
            if (user.getRole() == Role.ADMIN)
                return "redirect:/task-app/api/tasks/users-list";
            else if (user.getRole() == Role.USER){
                return "redirect:/task-app/api/tasks/"+user.getUsername();
            }
        }

        model.addAttribute("error", "Invalid username or password");
        return "login";
    }
    @PostMapping("/logout")
    public String logout(HttpSession httpSession) {
        if (httpSession != null){
            httpSession.invalidate();
        }
        return loginPage();
    }
}
