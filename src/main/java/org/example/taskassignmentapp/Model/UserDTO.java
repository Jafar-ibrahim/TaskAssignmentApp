package org.example.taskassignmentapp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.taskassignmentapp.Enum.Role;

@Data
@AllArgsConstructor
public class UserDTO {
    private String username;
    private Role role;
}
