package org.example.taskassignmentapp.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.taskassignmentapp.Model.User;
import org.example.taskassignmentapp.Model.UserDTO;
import org.example.taskassignmentapp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/task-app/api/users")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getRegisterView(Model model, HttpSession httpSession){
        User user = new User();
        model.addAttribute("user", user);
        return "doRegister";
    }

    @PreAuthorize("@authenticationService.hasRole(httpSession, 'ADMIN')")
    @PostMapping
    public String registerUser(@ModelAttribute User user,Model model, HttpSession httpSession){
        if (user != null){
            if (userService.userExists(user.getUsername())){
                model.addAttribute("error", "User already exists");
                return getRegisterView(model, httpSession);
            }
            Optional<UserDTO> userDTO = userService.addUser(user,"admin","admin");
            if (userDTO.isPresent()){
                model.addAttribute("success", "User registered successfully");
                return getRegisterView(model, httpSession);
            }
            model.addAttribute("error", "User registration failed");
        }
        model.addAttribute("error", "Invalid user details");
        return getRegisterView(model, httpSession);
    }
    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable("username") String username,
                             Model model) {
        if(userService.deleteUser(username, "admin", "admin")){
            model.addAttribute("success", "User {" + username + "} and their tasks have been deleted successfully");
        }else{
            model.addAttribute("error", "User deletion failed");
        }
        model.addAttribute("users", userService.getAllUsers("admin", "admin"));
        return "admin_users_list";
    }

}
