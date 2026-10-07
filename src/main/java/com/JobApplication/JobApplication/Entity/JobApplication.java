package com.JobApplication.JobApplication.Entity;

import com.JobApplication.JobApplication.Enum.ApplicationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class JobApplication
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String position;

    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    private ApplicationType status = ApplicationType.APPLIED;

    private String jobUrl;

    private BigDecimal salary;

    private String notes;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="company_id", nullable=false)
    private Company company;

}
