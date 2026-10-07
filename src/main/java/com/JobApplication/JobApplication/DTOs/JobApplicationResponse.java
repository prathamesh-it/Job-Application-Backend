package com.JobApplication.JobApplication.DTOs;

import com.JobApplication.JobApplication.Enum.ApplicationType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class JobApplicationResponse
{
    private long id;

    private String position;

    private LocalDate applicationDate;

    private ApplicationType status;

    private String jobUrl;

    private BigDecimal salary;

    private String notes;

    private Long userId;

    private Long companyId;
}
