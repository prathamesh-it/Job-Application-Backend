package com.JobApplication.JobApplication.Service;

import com.JobApplication.JobApplication.DTOs.LoginRequestDTO;
import com.JobApplication.JobApplication.DTOs.LoginResponse;
import com.JobApplication.JobApplication.DTOs.UserRequest;
import com.JobApplication.JobApplication.Entity.User;
import com.JobApplication.JobApplication.Mappers.UserMapper;
import com.JobApplication.JobApplication.Repository.UserRepository;
import com.JobApplication.JobApplication.Security.CustomUserDetails;
import com.JobApplication.JobApplication.Security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    //FOR LOGIN PURPOSE
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;


    public ResponseEntity<String> registerUser(UserRequest requestDTO)
    {
        if(userRepository.findByEmail(requestDTO.getEmail()).isPresent())   //if email is already present in the database dont register login
        {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exist.Please login."
            );
        }

        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword())); // Encode the password before saving

        user.setPhone(requestDTO.getPhone());
        user.setRole("USER");

        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");

    }

    public LoginResponse loginUser(LoginRequestDTO loginRequestDTO)
    {
        // throws AuthenticationException if the email or password is wrong

        authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       loginRequestDTO.getEmail(),
                       loginRequestDTO.getPassword()
               ));
        CustomUserDetails user = (CustomUserDetails) myUserDetailsService.loadUserByUsername(loginRequestDTO.getEmail());
        return new LoginResponse(jwtService.generateToken(user));

    }

//        Ya method madhye:
//        1. Email ani password gheto.
//        2. Authentication request tayar karto.
//        3. AuthenticationManager la deto.
//        4. Spring Security MyUserDetailsService madhun user shodhto.
//        5. PasswordEncoder encoded password verify karto.
//        6. Credentials chukle tar authentication exception throw hoto.
}
