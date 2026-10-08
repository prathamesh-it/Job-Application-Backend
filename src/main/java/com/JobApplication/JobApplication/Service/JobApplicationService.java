package com.JobApplication.JobApplication.Service;

import com.JobApplication.JobApplication.DTOs.CompanyResponse;
import com.JobApplication.JobApplication.DTOs.JobApplicationRequest;
import com.JobApplication.JobApplication.DTOs.JobApplicationResponse;
import com.JobApplication.JobApplication.Entity.Company;
import com.JobApplication.JobApplication.Entity.JobApplication;
import com.JobApplication.JobApplication.Entity.User;
import com.JobApplication.JobApplication.Enum.ApplicationType;
import com.JobApplication.JobApplication.Exceptions.CompanyNotFoundException;
import com.JobApplication.JobApplication.Exceptions.JobApplicationNotFoundByPositionException;
import com.JobApplication.JobApplication.Exceptions.JobApplicationNotFoundException;
import com.JobApplication.JobApplication.Exceptions.UserNotFoundException;
import com.JobApplication.JobApplication.Mappers.JobApplicationMapper;
import com.JobApplication.JobApplication.Repository.CompanyRepository;
import com.JobApplication.JobApplication.Repository.JobApplicationRepository;
import com.JobApplication.JobApplication.Repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public Page<JobApplicationResponse> getAllJobApplications(int page , int size , String sortBy , String direction)
    {
        Sort sort;

        if(direction.equalsIgnoreCase("asc"))
        {
             sort = Sort.by(sortBy).ascending();
        }
        else
        {
            sort = Sort.by(sortBy).descending();
        }
        Pageable pageable = PageRequest.of(page,size,sort);
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

    @Transactional
    public JobApplicationResponse updateJobApplication(long id,  JobApplicationRequest jobApplicationRequest)
    {
        //Find the user using user id
        User user = userRepository.findById(jobApplicationRequest.getUserId())
                .orElseThrow(()->new UserNotFoundException("User with id :"+jobApplicationRequest.getUserId()+ "not found"));

        //Find the company with company id

        Company company = companyRepository.findById(jobApplicationRequest.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company with id :"+jobApplicationRequest.getCompanyId()+"does not exist"));

        Optional<JobApplication> jobApplication = jobApplicationRepository.findById(id);

        JobApplication existingJobApplication = jobApplication.get();

        if(jobApplication.isPresent())
        {
            existingJobApplication.setApplicationDate(jobApplicationRequest.getApplicationDate());
            existingJobApplication.setPosition(jobApplicationRequest.getPosition());
            existingJobApplication.setSalary(jobApplicationRequest.getSalary());
            if(jobApplicationRequest.getStatus() != null)
            {
                existingJobApplication.setStatus(jobApplicationRequest.getStatus());
            }
            else
            {
                existingJobApplication.setStatus(existingJobApplication.getStatus());
            }
            existingJobApplication.setJobUrl(jobApplicationRequest.getJobUrl());
            existingJobApplication.setNotes(jobApplicationRequest.getNotes());
            existingJobApplication.setUser(user);
            existingJobApplication.setCompany(company);

        }
        return jobApplicationMapper.toResponse(jobApplicationRepository.save(existingJobApplication));

    }

    public String deleteApplicationById(long id)
    {
        Optional<JobApplication> jobApplication = jobApplicationRepository.findById(id);
        if(jobApplication.isPresent())
        {
            jobApplicationRepository.deleteById(id);
            return "JobApplication with id: " + id + " deleted successfully";
        }
        else
        {
            throw new JobApplicationNotFoundException("JobApplication with id: " + id + " not found");
        }
    }

    public List<JobApplicationResponse> getApplicationsByStatus(ApplicationType status)
    {
        List<JobApplication> jobApplications = jobApplicationRepository.findByStatus(status);
        if(jobApplications.isEmpty())
        {
            throw new JobApplicationNotFoundException("No JobApplications found with status: " + status);
        }
        List<JobApplicationResponse> jobApplicationResponses = new ArrayList<>();
        for(JobApplication jobApplication : jobApplications) {
            jobApplicationResponses.add(jobApplicationMapper.toResponse(jobApplication));
        }
        return jobApplicationResponses;
    }

    @Transactional
    public List<JobApplicationResponse> getApplicationsByCompanyId( long companyId)
    {
        Optional<Company> company = companyRepository.findById(companyId);

        if(!company.isPresent())
        {
            throw new CompanyNotFoundException("Company with id: " + companyId + " not found");
        }

        List<JobApplication> jobApplications = jobApplicationRepository.findJobApplicationByCompanyId(companyId);

        if(jobApplications.isEmpty())
        {
            throw new JobApplicationNotFoundException("No JobApplications found for company with id: " + companyId);
        }

        List<JobApplicationResponse> jobApplicationResponses = new ArrayList<>();
        for(JobApplication jobApplication : jobApplications)
        {
            jobApplicationResponses.add(jobApplicationMapper.toResponse(jobApplication));
        }

        return jobApplicationResponses;


    }

    public List<JobApplicationResponse> getApplicationsByPosition(String position)
    {
        List<JobApplication> jobApplications = jobApplicationRepository.findByPositionContainingIgnoreCase(position);

        if(jobApplications.isEmpty())
        {
            throw new JobApplicationNotFoundByPositionException("No JobApplications found for position: " + position);
        }

        List<JobApplicationResponse> jobApplicationResponses = new ArrayList<>();
        for(JobApplication jobApplication : jobApplications)
        {
            jobApplicationResponses.add(jobApplicationMapper.toResponse(jobApplication));
        }
        return jobApplicationResponses;
    }

    public List<JobApplicationResponse> getApplicationsBySalary(BigDecimal minSalary, BigDecimal maxSalary)
    {
        List<JobApplication> jobApplications = jobApplicationRepository.findBySalaryBetween(minSalary , maxSalary);

        if(jobApplications.isEmpty())
        {
            throw new JobApplicationNotFoundException("No JobApplications found betweem salary : " + minSalary + " and " + maxSalary);
        }

        List<JobApplicationResponse> jobApplicationResponses = new ArrayList<>();
        for(JobApplication jobApplication : jobApplications)
        {
            jobApplicationResponses.add(jobApplicationMapper.toResponse(jobApplication));
        }
        return jobApplicationResponses;
    }


}
