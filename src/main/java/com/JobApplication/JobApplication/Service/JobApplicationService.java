package com.JobApplication.JobApplication.Service;

import com.JobApplication.JobApplication.DTOs.JobApplicationRequest;
import com.JobApplication.JobApplication.DTOs.JobApplicationResponse;
import com.JobApplication.JobApplication.Entity.Company;
import com.JobApplication.JobApplication.Entity.JobApplication;
import com.JobApplication.JobApplication.Entity.User;
import com.JobApplication.JobApplication.Exceptions.JobApplicationNotFoundException;
import com.JobApplication.JobApplication.Mappers.JobApplicationMapper;
import com.JobApplication.JobApplication.Repository.CompanyRepository;
import com.JobApplication.JobApplication.Repository.JobApplicationRepository;
import com.JobApplication.JobApplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService
{
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobApplicationMapper jobApplicationMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public JobApplicationResponse addJobApplication(JobApplicationRequest jobApplicationRequest)
    {
        //CONVERT DTO TO ENTITY
        JobApplication jobApplication =
                jobApplicationMapper.toEntity(jobApplicationRequest
                );

        //Find the user using user id
        User user = userRepository.findById(jobApplicationRequest.getUserId())
                .orElseThrow(()->new RuntimeException("User with id :"+jobApplicationRequest.getUserId()+ "not found"));

        //Find the company with company id

        Company company = companyRepository.findById(jobApplicationRequest.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company with id :"+jobApplicationRequest.getCompanyId()+"does not exist"));



        //SET RELATIONSHIPS
        jobApplication.setUser(user);
        jobApplication.setCompany(company);

        //SAVE HERE
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);

        return jobApplicationMapper.toResponse(savedJobApplication);
    }

    public Page<JobApplicationResponse> getAllJobApplications(int page , int size)
    {
        Pageable pageable = PageRequest.of(page,size);
        return jobApplicationRepository.findAll(pageable)
                .map(jobApplicationMapper::toResponse);
    }

    public JobApplicationResponse getJobApplicationById(long id)
    {
        Optional<JobApplication> jobApplication = jobApplicationRepository.findById(id);
        if(jobApplication.isPresent())
        {
            return jobApplicationMapper.toResponse(jobApplication.get());
        }
        else
        {
            throw new JobApplicationNotFoundException("JobApplication with id: " + id + " not found");
        }

    }

    public String deleteApplicationById(long id)
    {
        if(jobApplicationRepository.existsById(id))
        {
            jobApplicationRepository.deleteById(id);
            return "JobApplication deleted successfully";
        }
        else
        {
            return "JobApplication with this id does not exist";
        }
    }
}
