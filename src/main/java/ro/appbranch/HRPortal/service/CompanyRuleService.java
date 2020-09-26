package ro.appbranch.HRPortal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ro.appbranch.HRPortal.entity.CompanyRule;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.entity.UserTimeOffInfo;
import ro.appbranch.HRPortal.repository.CompanyRuleRepository;
import ro.appbranch.HRPortal.repository.UserTimeOffInfoRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class CompanyRuleService {
    private final CompanyRuleRepository companyRuleRepository;
    private final UserTimeOffInfoRepository userTimeOffInfoRepository;

    @Autowired
    public CompanyRuleService(CompanyRuleRepository companyRuleRepository, UserTimeOffInfoRepository userTimeOffInfoRepository) {
        this.companyRuleRepository = companyRuleRepository;
        this.userTimeOffInfoRepository = userTimeOffInfoRepository;
    }

    public void executeCompanyRule(CompanyRule companyRule) {
        companyRule.setLastExecutionDate(LocalDate.now());
        companyRuleRepository.save(companyRule);

        List<User> usersToApplyRule = companyRule.getUsers().isEmpty() ? companyRule.getCompany().getUsers() : companyRule.getUsers();
        usersToApplyRule.forEach(user -> {
            var userTimeOff = userTimeOffInfoRepository.findByUserAndTimeOff(user, companyRule.getTimeOff())
                    .orElseGet(() -> new UserTimeOffInfo().setUser(user).setTimeOff(companyRule.getTimeOff()));

            switch (companyRule.getAction()) {
                case CompanyRule.ACTION_ADD -> userTimeOff.addNumberOfDays(companyRule.getDaysNumber());
                case CompanyRule.ACTION_SET -> userTimeOff.setCurrentNumberOfDays(companyRule.getDaysNumber());
                default -> throw new RuntimeException("Cronul de executie a regulilor de zile de concediu nu stie sa interpreteze actiunea cu id " + companyRule.getAction());
            }

            userTimeOffInfoRepository.save(userTimeOff);

        });
    }

    public boolean shouldExecuteRule(CompanyRule companyRule) {
        if (!ObjectUtils.isEmpty(companyRule.getLastExecutionDate())) {
            if (companyRule.getExecutionMoment().equals(CompanyRule.EXECUTION_MOMENT_BEGINNING_OF_MONTH)
                    && companyRule.getLastExecutionDate().getMonth().equals(LocalDate.now().getMonth())
            ) {
                return false;
            }

            return !companyRule.getExecutionMoment().equals(CompanyRule.EXECUTION_MOMENT_BEGINNING_OF_YEAR)
                    || companyRule.getLastExecutionDate().getYear() != LocalDate.now().getYear();
        }

        return true;
    }
}
