package ro.appbranch.HRPortal.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {
    @GetMapping("/")
    public String homepage(Model model) {
        return "frontend/homepage";
    }
}
