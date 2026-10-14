package com.JobApplication.JobApplication.Repository;

import com.JobApplication.JobApplication.Entity.JobApplication;
import com.JobApplication.JobApplication.Enum.ApplicationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByStatus(ApplicationType status);

    List<JobApplication> findJobApplicationByCompanyId(long companyId);

    List<JobApplication> findByPositionContainingIgnoreCase(String position);

    List<JobApplication> findBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary);

    Page<JobApplication> findByUserId(Long userId, Pageable pageable);

    List<JobApplication> findByUserIdAndStatus(Long userId, ApplicationType status);

    List<JobApplication> findByUserIdAndCompanyId(Long userId, Long companyId);

    List<JobApplication> findByUserIdAndPositionContainingIgnoreCase(Long userId, String position);

    List<JobApplication> findByUserIdAndSalaryBetween(Long userId, BigDecimal min, BigDecimal max);
}
