package com.JobApplication.JobApplication.Controllers;

import com.JobApplication.JobApplication.DTOs.JobApplicationRequest;
import com.JobApplication.JobApplication.DTOs.JobApplicationResponse;
import com.JobApplication.JobApplication.Service.JobApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-applications")
public class JobApplicationController
{
    @Autowired
    private JobApplicationService jobApplicationService;

    @PostMapping
    public JobApplicationResponse addJobApplication(@Valid @RequestBody JobApplicationRequest jobApplicationRequest)
    {
        System.out.println("Adding Job Application");
        return jobApplicationService.addJobApplication(jobApplicationRequest);
    }

    @GetMapping
    public Page<JobApplicationResponse> getAllJobApplications(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size)
    {
        System.out.println("Fetching all the Job Applications");
        return jobApplicationService.getAllJobApplications(page,size);
    }

    @GetMapping("/{id}")
    public JobApplicationResponse getJobApplicationById(@PathVariable long id)
    {
        System.out.println("Fetching JobApplication by id :"+id);
        return jobApplicationService.getJobApplicationById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteApplicationById(@PathVariable long id)
    {
        System.out.println("Deleting JobApplication of id :"+id);
        return jobApplicationService.deleteApplicationById(id);
    }


}
