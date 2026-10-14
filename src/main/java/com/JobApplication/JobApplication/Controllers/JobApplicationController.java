package com.JobApplication.JobApplication.Controllers;

import com.JobApplication.JobApplication.DTOs.CompanyResponse;
import com.JobApplication.JobApplication.DTOs.JobApplicationRequest;
import com.JobApplication.JobApplication.DTOs.JobApplicationResponse;
import com.JobApplication.JobApplication.Enum.ApplicationType;
import com.JobApplication.JobApplication.Security.CustomUserDetails;
import com.JobApplication.JobApplication.Service.JobApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasRole('USER')")  //only the user can add not even admin
    public JobApplicationResponse addJobApplication(@Valid @RequestBody JobApplicationRequest jobApplicationRequest,
                                                    @AuthenticationPrincipal CustomUserDetails currentUser)
    {
        System.out.println("Adding Job Application");
        return jobApplicationService.addJobApplication(jobApplicationRequest , currentUser);
    }

    //THIS ENDPOINT WILL HANDLE THE PAGINATION AS WELL AS THE SORTING PART ALSO
    //THIS ENDPOINT IS VERY IMPORTANT
    @GetMapping
    public Page<JobApplicationResponse> getAllJobApplications(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size,
                                                              @RequestParam(defaultValue = "id",required = false) String sortBy,
                                                              @RequestParam(defaultValue = "asc") String direction,
                                                              @AuthenticationPrincipal CustomUserDetails currentUser)
    {
        System.out.println("Fetching all the Job Applications");
        return jobApplicationService.getAllJobApplications(page,size,sortBy,direction, currentUser);
    }

    @GetMapping("/{id}")
    public JobApplicationResponse getJobApplicationById(@PathVariable long id,
                                                        @AuthenticationPrincipal CustomUserDetails currentUser)
    {
        System.out.println("Fetching JobApplication by id :"+id);
        return jobApplicationService.getJobApplicationById(id,currentUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public JobApplicationResponse updateJobApplication(@PathVariable long id,
                                                       @Valid @RequestBody JobApplicationRequest jobApplicationRequest,
                                                       @AuthenticationPrincipal CustomUserDetails currentUser)
    {
        System.out.println("Updating the Job Application");
        return jobApplicationService.updateJobApplication(id , jobApplicationRequest , currentUser);
    }

    @DeleteMapping("/{id}")
    public String deleteApplicationById(@PathVariable long id,
                                        @AuthenticationPrincipal CustomUserDetails currentUser)
    {
        System.out.println("Deleting JobApplication of id :"+id);
        return jobApplicationService.deleteApplicationById(id,currentUser);
    }

    @GetMapping("/status-check")
    public List<JobApplicationResponse> getApplicationsByStatus(@RequestParam ApplicationType status,
                                                                @AuthenticationPrincipal CustomUserDetails currentUser)
    {
        System.out.println("Fetching JobApplications by status :"+status);
        return jobApplicationService.getApplicationsByStatus(status,currentUser);
    }

    @GetMapping("/company")
    public List<JobApplicationResponse> getApplicationsByCompanyId(@RequestParam @Positive long companyId,
                                                                   @AuthenticationPrincipal CustomUserDetails currentUser)
    {
        System.out.println("Fetching JobApplications by company id :"+companyId);
        return jobApplicationService.getApplicationsByCompanyId(companyId,currentUser);
    }

    @GetMapping("/position")
    public List<JobApplicationResponse> getApplicationsByPosition(@RequestParam String position,
                                                                  @AuthenticationPrincipal CustomUserDetails currentUser)
    {
        System.out.println( "Fetching JobApplications by position :"+position);
        return jobApplicationService.getApplicationsByPosition(position,currentUser);
    }

    @GetMapping("/salary")
    public List<JobApplicationResponse> getApplicationsBySalary(@RequestParam BigDecimal minSalary , BigDecimal maxSalary,
                                                                @AuthenticationPrincipal CustomUserDetails currentUser)
    {
        System.out.println("Fetching JobApplications by salary range :"+minSalary+" - "+maxSalary);
        return jobApplicationService.getApplicationsBySalary(minSalary , maxSalary,currentUser);
    }


}
