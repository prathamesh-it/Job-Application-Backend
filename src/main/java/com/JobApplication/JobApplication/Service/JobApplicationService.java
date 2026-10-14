package com.JobApplication.JobApplication.Service;

import com.JobApplication.JobApplication.DTOs.JobApplicationRequest;
import com.JobApplication.JobApplication.DTOs.JobApplicationResponse;
import com.JobApplication.JobApplication.Entity.Company;
import com.JobApplication.JobApplication.Entity.JobApplication;
import com.JobApplication.JobApplication.Entity.User;
import com.JobApplication.JobApplication.Enum.ApplicationType;
import com.JobApplication.JobApplication.Enum.Role;
import com.JobApplication.JobApplication.Exceptions.CompanyNotFoundException;
import com.JobApplication.JobApplication.Exceptions.JobApplicationNotFoundByPositionException;
import com.JobApplication.JobApplication.Exceptions.JobApplicationNotFoundException;
import com.JobApplication.JobApplication.Exceptions.UserNotFoundException;
import com.JobApplication.JobApplication.Mappers.JobApplicationMapper;
import com.JobApplication.JobApplication.Repository.CompanyRepository;
import com.JobApplication.JobApplication.Repository.JobApplicationRepository;
import com.JobApplication.JobApplication.Repository.UserRepository;
import com.JobApplication.JobApplication.Security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobApplicationMapper jobApplicationMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private boolean isAdmin(CustomUserDetails u)
    {
        return u.getRole() == Role.ADMIN;
    }

    // adminAllowed = true mhanje ADMIN pan access karu shakto (view/delete), false mhanje fakt owner (edit)
    private JobApplication getAccessible(long id, CustomUserDetails currentUser,
                                         boolean adminAllowed)
    {
        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new JobApplicationNotFoundException("JobApplication with id: " + id + " not found"));

        boolean owner = app.getUser().getId().equals(currentUser.getId());
        boolean admin = adminAllowed && isAdmin(currentUser);

        if (!owner && !admin) {
            throw new AccessDeniedException("This is not your job application");
        }
        return app;
    }

    private List<JobApplicationResponse> toResponses(List<JobApplication> list,
                                                     RuntimeException ifEmpty)
    {
        if (list.isEmpty()) throw ifEmpty;
        return list.stream().map(jobApplicationMapper::toResponse).toList();
    }

    public JobApplicationResponse addJobApplication(JobApplicationRequest req,
                                                    CustomUserDetails currentUser)
    {
        JobApplication app = jobApplicationMapper.toEntity(req);

        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company with id: " + req.getCompanyId() + " does not exist"));

        app.setUser(userRepository.getReferenceById(currentUser.getId()));   // owner token madhun
        app.setCompany(company);

        return jobApplicationMapper.toResponse(jobApplicationRepository.save(app));
    }

    public Page<JobApplicationResponse> getAllJobApplications(int page, int size,
                                                              String sortBy,
                                                              String direction,
                                                              CustomUserDetails currentUser)
    {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JobApplication> result = isAdmin(currentUser)
                ? jobApplicationRepository.findAll(pageable)
                : jobApplicationRepository.findByUserId(currentUser.getId(), pageable);

        return result.map(jobApplicationMapper::toResponse);
    }

    public JobApplicationResponse getJobApplicationById(long id,
                                                        CustomUserDetails currentUser)
    {
        return jobApplicationMapper.toResponse(getAccessible(id, currentUser, true));
    }

    @Transactional
    public JobApplicationResponse updateJobApplication(long id, JobApplicationRequest req,
                                                       CustomUserDetails currentUser)
    {
        JobApplication existing = getAccessible(id, currentUser, false);   // fakt owner

        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company with id: " + req.getCompanyId() + " does not exist"));

        existing.setCompany(company);
        existing.setPosition(req.getPosition());
        existing.setSalary(req.getSalary());
        existing.setJobUrl(req.getJobUrl());
        existing.setNotes(req.getNotes());
        if (req.getApplicationDate() != null) existing.setApplicationDate(req.getApplicationDate());
        if (req.getStatus() != null) existing.setStatus(req.getStatus());
        // user badalat nahi, owner fixed rahto

        return jobApplicationMapper.toResponse(jobApplicationRepository.save(existing));
    }

    public String deleteApplicationById(long id,
                                        CustomUserDetails currentUser)
    {
        JobApplication app = getAccessible(id, currentUser, true);   // owner kivha admin
        jobApplicationRepository.delete(app);
        return "JobApplication with id: " + id + " deleted successfully";
    }

    public List<JobApplicationResponse> getApplicationsByStatus(ApplicationType status,
                                                                CustomUserDetails currentUser)
    {
        List<JobApplication> list = isAdmin(currentUser)
                ? jobApplicationRepository.findByStatus(status)
                : jobApplicationRepository.findByUserIdAndStatus(currentUser.getId(), status);
        return toResponses(list, new JobApplicationNotFoundException("No JobApplications found with status: " + status));
    }

    public List<JobApplicationResponse> getApplicationsByCompanyId(long companyId,
                                                                   CustomUserDetails currentUser)
    {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company with id: " + companyId + " not found");
        }
        List<JobApplication> list = isAdmin(currentUser)
                ? jobApplicationRepository.findJobApplicationByCompanyId(companyId)
                : jobApplicationRepository.findByUserIdAndCompanyId(currentUser.getId(), companyId);
        return toResponses(list, new JobApplicationNotFoundException("No JobApplications found for company with id: " + companyId));
    }

    public List<JobApplicationResponse> getApplicationsByPosition(String position,
                                                                  CustomUserDetails currentUser)
    {
        List<JobApplication> list = isAdmin(currentUser)
                ? jobApplicationRepository.findByPositionContainingIgnoreCase(position)
                : jobApplicationRepository.findByUserIdAndPositionContainingIgnoreCase(currentUser.getId(), position);
        return toResponses(list, new JobApplicationNotFoundByPositionException("No JobApplications found for position: " + position));
    }

    public List<JobApplicationResponse> getApplicationsBySalary(BigDecimal min,
                                                                BigDecimal max,
                                                                CustomUserDetails currentUser)
    {
        List<JobApplication> list = isAdmin(currentUser)
                ? jobApplicationRepository.findBySalaryBetween(min, max)
                : jobApplicationRepository.findByUserIdAndSalaryBetween(currentUser.getId(), min, max);
        return toResponses(list, new JobApplicationNotFoundException("No JobApplications found between salary: " + min + " and " + max));
    }
}
