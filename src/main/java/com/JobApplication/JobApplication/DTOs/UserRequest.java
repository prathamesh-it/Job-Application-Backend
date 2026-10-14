package com.JobApplication.JobApplication.DTOs;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest
{
    @NotBlank(message="Name is Required")
    private String name;

    @NotBlank(message = "Email is Required")
    @Email(message = "Please enter a valid email")
    private String email;

    @NotBlank(message = "Password is Required")
    @Size(min=6 , max = 20 , message="Password must be between 6 and 20 characters")
    private String password;

    @NotBlank(message = "Phone no is Required")
    @Pattern(
            regexp="^[0-9]{10}$" ,
            message="Phone no should contains exactly 10 digits"

    )
    private String phone;
}
