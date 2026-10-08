package com.JobApplication.JobApplication.Controllers;

import com.JobApplication.JobApplication.DTOs.CompanyResponse;
import com.JobApplication.JobApplication.DTOs.JobApplicationRequest;
import com.JobApplication.JobApplication.DTOs.JobApplicationResponse;
import com.JobApplication.JobApplication.Enum.ApplicationType;
import com.JobApplication.JobApplication.Service.JobApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    //THIS ENDPOINT WILL HANDLE THE PAGINATION AS WELL AS THE SORTING PART ALSO
    //THIS ENDPOINT IS VERY IMPORTANT
    @GetMapping
    public Page<JobApplicationResponse> getAllJobApplications(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size,
                                                              @RequestParam(defaultValue = "id",required = false) String sortBy,
                                                              @RequestParam(defaultValue = "asc") String direction)
    {
        System.out.println("Fetching all the Job Applications");
        return jobApplicationService.getAllJobApplications(page,size,sortBy,direction);
    }

    @GetMapping("/{id}")
    public JobApplicationResponse getJobApplicationById(@PathVariable long id)
    {
        System.out.println("Fetching JobApplication by id :"+id);
        return jobApplicationService.getJobApplicationById(id);
    }

    @PutMapping("/{id}")
    public JobApplicationResponse updateJobApplication(@PathVariable long id,
                                                       @Valid @RequestBody JobApplicationRequest jobApplicationRequest)
    {
        System.out.println("Updating the Job Application");
        return jobApplicationService.updateJobApplication(id , jobApplicationRequest);
    }

    @DeleteMapping("/{id}")
    public String deleteApplicationById(@PathVariable long id)
    {
        System.out.println("Deleting JobApplication of id :"+id);
        return jobApplicationService.deleteApplicationById(id);
    }

    @GetMapping("/status-check")
    public List<JobApplicationResponse> getApplicationsByStatus(@RequestParam ApplicationType status)
    {
        System.out.println("Fetching JobApplications by status :"+status);
        return jobApplicationService.getApplicationsByStatus(status);
    }

    @GetMapping("/company")
    public List<JobApplicationResponse> getApplicationsByCompanyId(@RequestParam @Positive long companyId)
    {
        System.out.println("Fetching JobApplications by company id :"+companyId);
        return jobApplicationService.getApplicationsByCompanyId(companyId);
    }

    @GetMapping("/position")
    public List<JobApplicationResponse> getApplicationsByPosition(@RequestParam String position)
    {
        System.out.println( "Fetching JobApplications by position :"+position);
        return jobApplicationService.getApplicationsByPosition(position);
    }

    @GetMapping("/salary")
    public List<JobApplicationResponse> getApplicationsBySalary(@RequestParam BigDecimal minSalary , BigDecimal maxSalary)
    {
        System.out.println("Fetching JobApplications by salary range :"+minSalary+" - "+maxSalary);
        return jobApplicationService.getApplicationsBySalary(minSalary , maxSalary);
    }


}
