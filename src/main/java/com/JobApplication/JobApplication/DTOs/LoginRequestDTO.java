package com.JobApplication.JobApplication.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO
{
    @NotBlank(message = "Email is Required")
    @Email(message = "Please enter a valid email")
    private String email;

    @NotBlank(message = "Password is Required")
    @Size(min=6 , message = "Password must be at least 6 characters")
    private String password;
}
