package com.JobApplication.JobApplication.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequest
{
    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Company location is required")
    private String location;

    @NotBlank(message = "Company website is required")
    private String website;
}
