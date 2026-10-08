package com.JobApplication.JobApplication.Repository;

import com.JobApplication.JobApplication.Entity.JobApplication;
import com.JobApplication.JobApplication.Enum.ApplicationType;
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
}
