package org.example.taskassignmentapp.Model;

import lombok.Data;
import org.example.taskassignmentapp.Enum.Role;

@Data
public class User {
    private String username;
    private String password;
    private Role role;
}
