package ro.appbranch.HRPortal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.appbranch.HRPortal.dto.company.SaveCompanyRequest;
import ro.appbranch.HRPortal.dto.company.SaveCompanyRuleRequest;
import ro.appbranch.HRPortal.entity.CompanyRule;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.repository.CompanyRepository;
import ro.appbranch.HRPortal.repository.CompanyRuleRepository;
import ro.appbranch.HRPortal.repository.TimeOffRepository;
import ro.appbranch.HRPortal.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyRuleRepository companyRuleRepository;
    private final TimeOffRepository timeOffRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, CompanyRuleRepository companyRuleRepository, TimeOffRepository timeOffRepository, UserRepository userRepository, UserService userService) {
        this.companyRepository = companyRepository;
        this.companyRuleRepository = companyRuleRepository;
        this.timeOffRepository = timeOffRepository;
        this.userRepository = userRepository;
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

    public void saveCompanyRules(SaveCompanyRuleRequest saveCompanyRuleRequest) {
        var company = userService.getLoggedUser().getCompany();

        CompanyRule companyRule;
        if (!saveCompanyRuleRequest.getRuleId().isEmpty()) {
            companyRule = companyRuleRepository.findById(Integer.parseInt(saveCompanyRuleRequest.getRuleId()))
                    .orElseThrow(() -> new RuntimeException("Regula cu id-ul " + saveCompanyRuleRequest.getRuleId() + " nu a fost gasita in baza de date!"));
        } else {
            companyRule = new CompanyRule();
        }

        List<User> userList = new ArrayList<>();
        saveCompanyRuleRequest.getUserIds().forEach(userId -> userRepository.findById(userId).ifPresent(userList::add));

        companyRule.setCompany(company)
                .setAction(saveCompanyRuleRequest.getAction())
                .setExecutionMoment(saveCompanyRuleRequest.getExecutionMoment())
                .setDaysNumber(saveCompanyRuleRequest.getDaysNumber())
                .setCumulateYears(saveCompanyRuleRequest.isCumulateYears())
                .setTimeOff(
                        timeOffRepository.findByStatusTrueAndId(saveCompanyRuleRequest.getTimeOffId())
                                .orElseThrow(() -> new RuntimeException("Tipul de zi de concediu cu id-ul " + saveCompanyRuleRequest.getTimeOffId() + " nu a fost gasit in baza de date"))
                )
                .setUsers(userList);

        companyRuleRepository.save(companyRule);
    }
}