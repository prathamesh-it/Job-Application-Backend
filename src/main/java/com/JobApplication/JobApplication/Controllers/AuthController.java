package com.JobApplication.JobApplication.Controllers;

import com.JobApplication.JobApplication.DTOs.LoginRequestDTO;
import com.JobApplication.JobApplication.DTOs.UserRequest;
import com.JobApplication.JobApplication.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class AuthController
{
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequest requestDTO)
    {
        System.out.println("Registering User with name: " + requestDTO.getName() + ", email: " + requestDTO.getEmail());
        return authService.registerUser(requestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO)
    {
        try
        {
            return ResponseEntity.ok(authService.loginUser(loginRequestDTO));
        }
        catch(AuthenticationException ex)
        {
            //401 -  Unauthorized
            return ResponseEntity.status(401).body("Invalid email or password");
        }

    }
}
