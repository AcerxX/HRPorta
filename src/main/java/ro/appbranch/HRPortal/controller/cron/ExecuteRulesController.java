package ro.appbranch.HRPortal.controller.cron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import ro.appbranch.HRPortal.config.HRPTransactional;
import ro.appbranch.HRPortal.repository.CompanyRuleRepository;
import ro.appbranch.HRPortal.service.CompanyRuleService;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class ExecuteRulesController extends BaseCronController {
    private static final int IGNORE_LAST_EXECUTION_DATE = 1;

    private final CompanyRuleRepository companyRuleRepository;
    private final CompanyRuleService companyRuleService;

    @Autowired
    public ExecuteRulesController(CompanyRuleRepository companyRuleRepository, CompanyRuleService companyRuleService) {
        this.companyRuleRepository = companyRuleRepository;
        this.companyRuleService = companyRuleService;
    }

    @HRPTransactional
    @ResponseBody
    @GetMapping("/execute-rule/{companyRuleId}/{ignoreLastExecutionDate}")
    public String executeRule(@PathVariable Integer companyRuleId, @PathVariable Integer ignoreLastExecutionDate) {
        resetLog();

        var companyRule = companyRuleRepository.findById(companyRuleId)
                .orElseThrow(() -> new RuntimeException("Regula cu ID-ul " + companyRuleId + " nu a fost gasita in baza e date!"));

        this.addLogToCronLog("[TRY] Se incearca executarea regulii " + companyRule.getId());

        if (ignoreLastExecutionDate.equals(IGNORE_LAST_EXECUTION_DATE) || companyRuleService.shouldExecuteRule(companyRule)) {
            companyRuleService.executeCompanyRule(companyRule);
        } else {
            this.addLogToCronLog("[SKIP] Nu se va executa regula " + companyRule.getId() + " pentru ca a fost deja executata in perioada aceasta!");
        }

        this.addLogToCronLog("[END] Regula a fost aplicata!");

        return this.getCronLog();
    }


    @ResponseBody
    @GetMapping("/execute-rules-cron/{executionMoment}/")
    @HRPTransactional()
    public String executeRulesCron(@PathVariable Integer executionMoment) {
        resetLog();

        AtomicInteger numberOfExecutions = new AtomicInteger();

        var toBeAppliedCompanyRules = companyRuleRepository.findAllByExecutionMoment(executionMoment);

        toBeAppliedCompanyRules.forEach(companyRule -> {
            this.addLogToCronLog("[TRY] Se incearca executarea regulii " + companyRule.getId());

            if (companyRuleService.shouldExecuteRule(companyRule)) {
                companyRuleService.executeCompanyRule(companyRule);

                numberOfExecutions.getAndIncrement();
                this.addLogToCronLog("[EXECUTED] A fost executata regula " + companyRule.getId());
            } else {
                this.addLogToCronLog("[SKIP] Nu se va executa regula " + companyRule.getId() + " pentru ca a fost deja executata in perioada aceasta!");
            }
        });

        this.addLogToCronLog("[END] Au fost aplicate " + numberOfExecutions.get() + " reguli.");

        return this.getCronLog();
    }
}
