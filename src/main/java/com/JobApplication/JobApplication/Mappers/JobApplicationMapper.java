package com.JobApplication.JobApplication.Mappers;

import com.JobApplication.JobApplication.DTOs.JobApplicationRequest;
import com.JobApplication.JobApplication.DTOs.JobApplicationResponse;
import com.JobApplication.JobApplication.Entity.JobApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class JobApplicationMapper
{
    public JobApplication toEntity(JobApplicationRequest jobApplicationRequest)
    {
        JobApplication jobApplication = new JobApplication();

        jobApplication.setPosition(jobApplicationRequest.getPosition());
        if(jobApplicationRequest.getApplicationDate() != null)
        {
            jobApplication.setApplicationDate(jobApplicationRequest.getApplicationDate());
        }
        else {
            jobApplication.setApplicationDate(LocalDate.now());
        }
        jobApplication.setStatus(jobApplicationRequest.getStatus());
        jobApplication.setJobUrl(jobApplicationRequest.getJobUrl());
        jobApplication.setSalary(jobApplicationRequest.getSalary());
        jobApplication.setNotes(jobApplicationRequest.getNotes());

        return jobApplication;
    }

    public JobApplicationResponse toResponse(JobApplication jobApplication)
    {
        JobApplicationResponse jobApplicationResponse = new JobApplicationResponse();

        jobApplicationResponse.setId(jobApplication.getId());
        jobApplicationResponse.setPosition(jobApplication.getPosition());
        jobApplicationResponse.setApplicationDate(jobApplication.getApplicationDate());
        jobApplicationResponse.setStatus(jobApplication.getStatus());
        jobApplicationResponse.setJobUrl(jobApplication.getJobUrl());
        jobApplicationResponse.setSalary(jobApplication.getSalary());
        jobApplicationResponse.setNotes(jobApplication.getNotes());

        jobApplicationResponse.setUserId(jobApplication.getUser().getId());
        jobApplicationResponse.setCompanyId(jobApplication.getCompany().getId());

        return jobApplicationResponse;
    }


}
