package com.JobApplication.JobApplication.DTOs;

import com.JobApplication.JobApplication.Enum.ApplicationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class JobApplicationRequest
{
    @NotBlank(message = "Position is mandatory")
    private String position;

    private LocalDate applicationDate;

    private ApplicationType status;

    @NotBlank(message = "Job URL is mandatory")
    private String jobUrl;

    @NotBlank(message = "Notes are mandatory")
    private String notes;

    @NotNull(message = "Salary is mandatory")
    @Positive(message = "Salary must be a positive number")
    private BigDecimal salary;

    @NotNull(message = "User ID is mandatory")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @NotNull(message = "Company ID is mandatory")
    @Positive(message = "Company Id must be a positive number")
    private Long companyId;

}
