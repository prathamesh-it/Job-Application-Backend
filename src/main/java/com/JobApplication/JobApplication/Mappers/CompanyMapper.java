package com.JobApplication.JobApplication.Mappers;

import com.JobApplication.JobApplication.DTOs.CompanyRequest;
import com.JobApplication.JobApplication.DTOs.CompanyResponse;
import com.JobApplication.JobApplication.Entity.Company;
import com.JobApplication.JobApplication.Repository.CompanyRepository;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper
{
    public Company toEntity(CompanyRequest companyRequest)
    {
        Company company = new Company();

        company.setName(companyRequest.getName());
        company.setLocation(companyRequest.getLocation());
        company.setWebsite(companyRequest.getWebsite());

        return company;
    }

    public CompanyResponse toResponse(Company company)
    {
        CompanyResponse companyResponse = new CompanyResponse();

        companyResponse.setId(company.getId());
        companyResponse.setName(company.getName());
        companyResponse.setLocation(company.getLocation());
        companyResponse.setWebsite(company.getWebsite());

        return companyResponse;
    }
}
