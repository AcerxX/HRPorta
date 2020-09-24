package ro.appbranch.HRPortal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ro.appbranch.HRPortal.dto.BaseResponse;
import ro.appbranch.HRPortal.dto.company.SaveCompanyRequest;
import ro.appbranch.HRPortal.dto.company.SaveCompanyRuleRequest;
import ro.appbranch.HRPortal.repository.TimeOffRepository;
import ro.appbranch.HRPortal.service.CompanyService;

@Controller
@RequestMapping("/company")
public class CompanyController extends SecuredController {
    private final CompanyService companyService;
    private final TimeOffRepository timeOffRepository;

    @Autowired
    public CompanyController(CompanyService companyService, TimeOffRepository timeOffRepository) {
        this.companyService = companyService;
        this.timeOffRepository = timeOffRepository;
    }

    @GetMapping("/edit")
    public String editCompany(Model model) {
        model.addAttribute("company", getLoggedUser().getCompany());
        model.addAttribute("timeOffList", timeOffRepository.findAllByStatusTrueOrderBySortOrderAsc());

        return "settings/company";
    }

    @ResponseBody()
    @PostMapping("/update")
    public BaseResponse saveCompany(@RequestBody SaveCompanyRequest saveCompanyRequest) {
        companyService.saveCompany(saveCompanyRequest);

        return new BaseResponse();
    }

    @ResponseBody()
    @PostMapping("/update-rules")
    public BaseResponse saveCompanyRules(@RequestBody SaveCompanyRuleRequest saveCompanyRuleRequest) {
        companyService.saveCompanyRules(saveCompanyRuleRequest);

        return new BaseResponse();
    }
}
