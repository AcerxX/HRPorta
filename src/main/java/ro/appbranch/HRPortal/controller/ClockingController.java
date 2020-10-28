package ro.appbranch.HRPortal.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ro.appbranch.HRPortal.entity.TimeOff;
import ro.appbranch.HRPortal.entity.UserTimeOffInfo;
import ro.appbranch.HRPortal.entity.UserTimeOffLog;
import ro.appbranch.HRPortal.repository.HolidayRepository;
import ro.appbranch.HRPortal.repository.TimeOffRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/clocking-table")
public class ClockingController extends SecuredController {
    private final TimeOffRepository timeOffRepository;
    private final HolidayRepository holidayRepository;

    @Autowired
    public ClockingController(TimeOffRepository timeOffRepository, HolidayRepository holidayRepository) {
        this.timeOffRepository = timeOffRepository;
        this.holidayRepository = holidayRepository;
    }

    @GetMapping("/")
    public String homepage(Model model) {
        var a = IntStream.range(1, 32).boxed().collect(Collectors.toList());
        var b = LocalDate.now().lengthOfMonth();

        model.addAttribute("days", a);
        return "clocking/clockingTable";
    }
}
