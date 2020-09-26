package ro.appbranch.HRPortal.controller.cron;

import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import ro.appbranch.HRPortal.config.HRPTransactional;
import ro.appbranch.HRPortal.entity.CompanyRule;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.entity.UserTimeOffInfo;
import ro.appbranch.HRPortal.repository.CompanyRuleRepository;
import ro.appbranch.HRPortal.repository.UserTimeOffInfoRepository;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class ExecuteRulesController extends BaseCronController {
    private final UserTimeOffInfoRepository userTimeOffInfoRepository;
    private final CompanyRuleRepository companyRuleRepository;

    public ExecuteRulesController(UserTimeOffInfoRepository userTimeOffInfoRepository, CompanyRuleRepository companyRuleRepository) {
        this.userTimeOffInfoRepository = userTimeOffInfoRepository;
        this.companyRuleRepository = companyRuleRepository;
    }


    @ResponseBody
    @GetMapping("/execute-monthly-rules/{executionMoment}")
    @HRPTransactional()
    public String executeMonthlyRules(@PathVariable Integer executionMoment) {
        resetLog();

        AtomicInteger numberOfExecutions = new AtomicInteger();
        AtomicBoolean skipRunning = new AtomicBoolean(false);

        var toBeAppliedCompanyRules = companyRuleRepository.findAllByExecutionMoment(executionMoment);

        toBeAppliedCompanyRules.forEach(companyRule -> {
            this.addLogToCronLog("[TRY] Se incearca rularea regulii " + companyRule.getId());

            switch (companyRule.getExecutionMoment()) {
                case CompanyRule.EXECUTION_MOMENT_BEGINNING_OF_MONTH -> {
                    if (!ObjectUtils.isEmpty(companyRule.getLastExecutionDate())
                            && companyRule.getLastExecutionDate().getMonth().equals(LocalDate.now().getMonth())
                    ) {
                        this.addLogToCronLog("[SKIP] Nu se va executa regula " + companyRule.getId() + " pentru ca a fost deja executata in luna " + companyRule.getLastExecutionDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                        skipRunning.set(true);
                    }
                }
                case CompanyRule.EXECUTION_MOMENT_BEGINNING_OF_YEAR -> {
                    if (!ObjectUtils.isEmpty(companyRule.getLastExecutionDate())
                            && companyRule.getLastExecutionDate().getYear() == LocalDate.now().getYear()
                    ) {
                        this.addLogToCronLog("[SKIP] Nu se va executa regula " + companyRule.getId() + " pentru ca a fost deja executata in anul " + companyRule.getLastExecutionDate().getYear());
                        skipRunning.set(true);
                    }
                }
            }

            if (!skipRunning.get()) {
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
                    numberOfExecutions.getAndIncrement();
                });

                this.addLogToCronLog("[EXECUTED] A fost executata regula " + companyRule.getId());
            }
        });

        this.addLogToCronLog("[END] Au fost modificate " + numberOfExecutions.get() + " zile de concediu.");

        return this.getCronLog();
    }
}
