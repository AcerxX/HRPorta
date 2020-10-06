package ro.appbranch.HRPortal.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ro.appbranch.HRPortal.entity.TimeOff;
import ro.appbranch.HRPortal.entity.UserTimeOffInfo;
import ro.appbranch.HRPortal.repository.HolidayRepository;
import ro.appbranch.HRPortal.repository.TimeOffRepository;

@Controller
public class DashboardController extends SecuredController {
    private final TimeOffRepository timeOffRepository;
    private final HolidayRepository holidayRepository;

    @Autowired
    public DashboardController(TimeOffRepository timeOffRepository, HolidayRepository holidayRepository) {
        this.timeOffRepository = timeOffRepository;
        this.holidayRepository = holidayRepository;
    }

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

        model.addAttribute("timeOffList", timeOffRepository.findAllByStatusTrueOrderBySortOrderAsc());

        var holidayList = holidayRepository.findAll();
        model.addAttribute("holidayListJson", new Gson().toJson(holidayList));
        model.addAttribute("holidayList", holidayList);

        return "dashboard";
    }
}
