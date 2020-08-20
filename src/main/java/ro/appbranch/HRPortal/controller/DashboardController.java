package ro.appbranch.HRPortal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController extends SecuredController {
    @GetMapping("/")
    public String homepage(Model model) {
        return "dashboard";
    }
}
