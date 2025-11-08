// AuthResponse.java
package com.disasterrelief.relief_system.dto;

import com.disasterrelief.relief_system.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private User.UserRole role;

    public AuthResponse(String token, Long id, String name, String email, User.UserRole role) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
