package ro.appbranch.HRPortal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.appbranch.HRPortal.dto.company.SaveCompanyRequest;
import ro.appbranch.HRPortal.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserService userService;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, UserService userService) {
        this.companyRepository = companyRepository;
        this.userService = userService;
    }

    public void saveCompany(SaveCompanyRequest saveCompanyRequest) {
        var company = userService.getLoggedUser().getCompany();

        if (!company.getEmailDomain().equals(saveCompanyRequest.getEmailDomain())) {
            userService.changeEmailDomainForUsers(company.getUsers(), saveCompanyRequest.getEmailDomain());
        }

        company.setEmailDomain(saveCompanyRequest.getEmailDomain())
                .setName(saveCompanyRequest.getName());

        companyRepository.save(company);
    }
}