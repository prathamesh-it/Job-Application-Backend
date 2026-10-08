package com.JobApplication.JobApplication.Service;

import com.JobApplication.JobApplication.DTOs.CompanyRequest;
import com.JobApplication.JobApplication.DTOs.CompanyResponse;
import com.JobApplication.JobApplication.Entity.Company;
import com.JobApplication.JobApplication.Exceptions.CompanyNotFoundException;
import com.JobApplication.JobApplication.Mappers.CompanyMapper;
import com.JobApplication.JobApplication.Repository.CompanyRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService
{
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    public CompanyResponse addCompany(@Valid CompanyRequest companyRequest)
    {
         return companyMapper.toResponse(
                 companyRepository.save(
                         companyMapper.toEntity(companyRequest)
                 )
         );
    }


    public Page<CompanyResponse> getAllCompanies(int page , int size)
    {
        Pageable pageable = PageRequest.of(page , size);
        Page<Company> companyPage = companyRepository.findAll(pageable);
        return companyPage.map(companyMapper::toResponse);
    }

    public CompanyResponse getCompanyById(long id)
    {
        Optional<Company> company = companyRepository.findById(id);
        if(company.isPresent())
        {
            return companyMapper.toResponse(company.get());
        }
        else
        {
            throw new CompanyNotFoundException("Company with id: " + id + " not found");
        }
    }


    @Transactional
    public CompanyResponse updateCompanyById(long id, CompanyRequest companyRequest)
    {
        Company company = companyRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("Company with "+id +"  id not found"));

        company.setName(companyRequest.getName());
        company.setLocation(companyRequest.getLocation());
        company.setWebsite(companyRequest.getWebsite());

        return  companyMapper.toResponse(
                companyRepository.save(company)
        );
    }

    public String deleteCompanyById(long id)
    {
        if(companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return "Company deleted Sucessfully";
        }
        else {
            throw new RuntimeException("Company with id: " + id + " not found");
        }

    }
}
