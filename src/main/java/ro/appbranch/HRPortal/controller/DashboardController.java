package ro.appbranch.HRPortal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ro.appbranch.HRPortal.entity.TimeOff;
import ro.appbranch.HRPortal.entity.UserTimeOffInfo;

@Controller
public class DashboardController extends SecuredController {
    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute(
                "holidayCurrentDays",
                this.getLoggedUser()
                        .getUserTimeOffInfoList()
                        .stream()
                        .filter(userTimeOffInfo -> userTimeOffInfo.getTimeOff().getId().equals(TimeOff.ID_CO))
                        .mapToDouble(UserTimeOffInfo::getCurrentNumberOfDays)
                        .sum()
        );
        model.addAttribute(
                "wfhCurrentDays",
                this.getLoggedUser()
                        .getUserTimeOffInfoList()
                        .stream()
                        .filter(userTimeOffInfo -> userTimeOffInfo.getTimeOff().getId().equals(TimeOff.ID_WFH))
                        .mapToDouble(UserTimeOffInfo::getCurrentNumberOfDays)
                        .sum()
        );

        return "dashboard";
    }
}
