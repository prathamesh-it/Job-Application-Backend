package com.JobApplication.JobApplication.Service;

import com.JobApplication.JobApplication.DTOs.CompanyRequest;
import com.JobApplication.JobApplication.DTOs.CompanyResponse;
import com.JobApplication.JobApplication.Entity.Company;
import com.JobApplication.JobApplication.Enum.Role;
import com.JobApplication.JobApplication.Exceptions.CompanyNotFoundException;
import com.JobApplication.JobApplication.Mappers.CompanyMapper;
import com.JobApplication.JobApplication.Repository.CompanyRepository;
import com.JobApplication.JobApplication.Repository.UserRepository;
import com.JobApplication.JobApplication.Security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyService
{
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private UserRepository userRepository;

    public CompanyResponse addCompany( CompanyRequest companyRequest, CustomUserDetails currentUser)
    {
         Company company = companyMapper.toEntity(companyRequest);
         company.setCreatedBy(userRepository.getReferenceById(currentUser.getId()));//jpa return proxy instead of entire entity
        return companyMapper.toResponse(companyRepository.save(company));
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
    public CompanyResponse updateCompanyById(long id, CompanyRequest companyRequest, CustomUserDetails currentUser)
    {
        Company company = companyRepository.findById(id)
                .orElseThrow(() ->  new RuntimeException("Company with "+id +"  id not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if(!isOwner(company , currentUser) && !isAdmin)   //owner pn nahi ahe admin pn nahi
        {
                throw new AccessDeniedException("Only the owner of Admin can delete this company");
        }

        company.setName(companyRequest.getName());
        company.setLocation(companyRequest.getLocation());
        company.setWebsite(companyRequest.getWebsite());

        return  companyMapper.toResponse(
                companyRepository.save(company)
        );
    }

    private boolean isOwner(Company company, CustomUserDetails currentUser)
    {
        return company.getCreatedBy() != null &&
                company.getCreatedBy().getId().equals(currentUser.getId());
    }

    @Transactional
    public String deleteCompanyById(long id, CustomUserDetails currentUser)
    {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Comapny with id: "+id+" does not exist"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner(company, currentUser) && !isAdmin)
        {
            throw new AccessDeniedException("Only the owner or an admin can delete this company");
        }

        companyRepository.delete(company);// aslo delete all the application associated to it
        return "Company with id: "+id+" deleted successfully";

    }
}
