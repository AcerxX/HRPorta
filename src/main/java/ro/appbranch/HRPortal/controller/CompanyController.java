package ro.appbranch.HRPortal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class CompanyController extends SecuredController {

    @GetMapping("/edit")
    public String homepage(Model model) {
        model.addAttribute("company", getLoggedUser().getCompany());

        return "settings/company";
    }
}
