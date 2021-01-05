package ro.appbranch.HRPortal.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ro.appbranch.HRPortal.entity.TimeOff;
import ro.appbranch.HRPortal.entity.UserTimeOffInfo;
import ro.appbranch.HRPortal.entity.UserTimeOffLog;
import ro.appbranch.HRPortal.repository.HolidayRepository;
import ro.appbranch.HRPortal.repository.TimeOffRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                (int) Math.floor(
                        this.getLoggedUser()
                                .getUserTimeOffInfoList()
                                .stream()
                                .filter(userTimeOffInfo -> userTimeOffInfo.getTimeOff().getId().equals(TimeOff.ID_CO))
                                .mapToDouble(UserTimeOffInfo::getCurrentNumberOfDays)
                                .sum()
                )
        );
        model.addAttribute(
                "wfhCurrentDays",
                (int) Math.floor(
                        this.getLoggedUser()
                                .getUserTimeOffInfoList()
                                .stream()
                                .filter(userTimeOffInfo -> userTimeOffInfo.getTimeOff().getId().equals(TimeOff.ID_WFH))
                                .mapToDouble(UserTimeOffInfo::getCurrentNumberOfDays)
                                .sum()
                )
        );

        model.addAttribute("timeOffList", timeOffRepository.findAllByStatusTrueOrderBySortOrderAsc());

        var holidayList = holidayRepository.findAll();
        model.addAttribute("holidayListJson", new Gson().toJson(holidayList));
        model.addAttribute("holidayList", holidayList);

        List<Map<String, String>> userTimeOffLogs = new ArrayList<>();
        this.getLoggedUser().getUserTimeOffLogs()
                .forEach(userTimeOffLog -> {
                    if (!userTimeOffLog.getStatus().equals(UserTimeOffLog.STATUS_DECLINED)) {
                        userTimeOffLog.getStartDate().datesUntil(userTimeOffLog.getEndDate().plusDays(1))
                                .forEach(localDate -> {
                                    Map<String, String> temp = new HashMap<>();
                                    temp.put("date", localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                                    temp.put("text", "Concediu " + (userTimeOffLog.getStatus().equals(UserTimeOffLog.STATUS_APPROVED) ? "aprobat" : "neaprobat"));

                                    userTimeOffLogs.add(temp);
                                });
                    }
                });
        model.addAttribute("userTimeOffLogsJson", new Gson().toJson(userTimeOffLogs));
        model.addAttribute("userTimeOffLogsArrayList", userTimeOffLogs);

        return "dashboard";
    }

    @GetMapping("/document-test")
    public String documentTest() {
        return "documentTest";
    }
}
