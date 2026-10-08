package com.JobApplication.JobApplication.Controllers;

import com.JobApplication.JobApplication.DTOs.CompanyRequest;
import com.JobApplication.JobApplication.DTOs.CompanyResponse;
import com.JobApplication.JobApplication.Entity.Company;
import com.JobApplication.JobApplication.Service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController
{
    @Autowired
    private CompanyService companyService;

    @PostMapping
    public CompanyResponse addCompany(@Valid @RequestBody CompanyRequest companyRequest)
    {
        System.out.println("CompanyController: addCompany called with request: " + companyRequest);
        return companyService.addCompany(companyRequest);
    }

    @GetMapping
    public Page<CompanyResponse> getAlCompanies(@RequestParam(defaultValue = "0")int page,
                                                @RequestParam(defaultValue = "3")int size)
    {
        System.out.println("Fetching all the companies");
        return companyService.getAllCompanies(page,size);
    }

    @GetMapping("/{id}")
    public CompanyResponse getCompanyById(@PathVariable long id)
    {
        System.out.println("Fetching company with id: " + id);
        return companyService.getCompanyById(id);
    }

    @PutMapping("/{id}")
    public CompanyResponse updateCompanyById(@PathVariable long id,
                                             @Valid @RequestBody CompanyRequest companyRequest)
    {
        System.out.println("Updating company with id: "+id);
        return companyService.updateCompanyById(id , companyRequest);
    }

    @DeleteMapping("/{id}")
    public String deleteCompanyById(@PathVariable long id)
    {
        return companyService.deleteCompanyById(id);
    }


}
