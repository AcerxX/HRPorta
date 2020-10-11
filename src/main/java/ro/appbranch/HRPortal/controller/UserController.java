package ro.appbranch.HRPortal.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.appbranch.HRPortal.dto.BaseResponse;
import ro.appbranch.HRPortal.dto.user.SaveUserRequest;
import ro.appbranch.HRPortal.entity.TimeOff;
import ro.appbranch.HRPortal.entity.UserTimeOffInfo;
import ro.appbranch.HRPortal.entity.UserTimeOffLog;
import ro.appbranch.HRPortal.repository.*;
import ro.appbranch.HRPortal.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class UserController extends SecuredController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final TeamRepository teamRepository;
    private final RoleRepository roleRepository;
    private final TimeOffRepository timeOffRepository;
    private final HolidayRepository holidayRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, JobRepository jobRepository, TeamRepository teamRepository, RoleRepository roleRepository, TimeOffRepository timeOffRepository, HolidayRepository holidayRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.teamRepository = teamRepository;
        this.roleRepository = roleRepository;
        this.timeOffRepository = timeOffRepository;
        this.holidayRepository = holidayRepository;
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your email or password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @GetMapping("/user/list")
    public String userList(Model model) {
        model.addAttribute("allUsers", userRepository.findAllByStatusTrueOrderByFullNameAsc());

        return "user/userList";
    }

    @GetMapping("/user/add")
    public String addUserInterface(Model model) {
        model.addAttribute("allUsers", userRepository.findAllByStatusTrueOrderByFullNameAsc());
        model.addAttribute("allJobs", jobRepository.findAllByOrderByNameAsc());
        model.addAttribute("allTeams", teamRepository.findAllByOrderByNameAsc());
        model.addAttribute("allRoles", roleRepository.findAll());

        return "user/addUser";
    }

    @ResponseBody()
    @PostMapping("/user/update")
    public BaseResponse saveUser(@RequestBody SaveUserRequest saveUserRequest) {
        var response = new BaseResponse();

        userService.saveUser(saveUserRequest);

        return response;
    }


    @GetMapping("/user/info/{id}")
    public String userInfo(Model model, @PathVariable Integer id, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {
        var userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE_FLASH_KEY, "Utilizatorul cu id-ul " + id + " nu a fost gasit in baza de date!");

            var lastPage = httpServletRequest.getHeader("Referer");

            if (ObjectUtils.isEmpty(lastPage)) {
                lastPage = "/";
            }

            return "redirect:" + lastPage;
        }
        var user = userOptional.get();

        model.addAttribute("user", user);
        model.addAttribute("allUsers", userRepository.findAllByStatusTrueOrderByFullNameAsc());
        model.addAttribute("allJobs", jobRepository.findAllByOrderByNameAsc());
        model.addAttribute("allTeams", teamRepository.findAllByOrderByNameAsc());
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("userTimeOffLogs", userOptional.get().getUserTimeOffLogs());

        model.addAttribute("timeOffList", timeOffRepository.findAllByStatusTrueOrderBySortOrderAsc());

        var holidayList = holidayRepository.findAll();
        model.addAttribute("holidayListJson", new Gson().toJson(holidayList));
        model.addAttribute("holidayList", holidayList);

        List<Map<String, String>> userTimeOffLogs = new ArrayList<>();
        user.getUserTimeOffLogs()
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

        model.addAttribute(
                "holidayCurrentDays",
                (int) Math.floor(
                        user
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
                        user
                                .getUserTimeOffInfoList()
                                .stream()
                                .filter(userTimeOffInfo -> userTimeOffInfo.getTimeOff().getId().equals(TimeOff.ID_WFH))
                                .mapToDouble(UserTimeOffInfo::getCurrentNumberOfDays)
                                .sum()
                )
        );

        model.addAttribute(
                "lastYearRemainedCO",
                (int) Math.floor(
                        user
                                .getUserTimeOffInfoList()
                                .stream()
                                .filter(userTimeOffInfo -> userTimeOffInfo.getTimeOff().getId().equals(TimeOff.ID_CO))
                                .mapToDouble(UserTimeOffInfo::getLastPeriodNumberOfDays)
                                .sum()
                )
        );

        model.addAttribute(
                "thisYearTotalCO",
                user
                        .getUserTimeOffInfoList()
                        .stream()
                        .filter(userTimeOffInfo -> userTimeOffInfo.getTimeOff().getId().equals(TimeOff.ID_CO))
                        .mapToDouble(UserTimeOffInfo::getCurrentMaxNumberOfDays)
                        .sum()
        );

        var nextTimeOff = user
                .getUserTimeOffLogs()
                .stream()
                .filter(userTimeOffLog -> userTimeOffLog.getStartDate().isAfter(LocalDate.now()) && userTimeOffLog.getStatus().equals(UserTimeOffLog.STATUS_APPROVED))
                .min(Comparator.comparing(UserTimeOffLog::getStartDate));
        model.addAttribute(
                "nextTimeOff",
                nextTimeOff.isPresent() ? nextTimeOff.get().getStartDateAsString() : "Neplanificat"
        );


        return "user/userProfile";
    }

    @GetMapping("/my-profile")
    public String myProfile(Model model, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {
        return userInfo(model, getLoggedUser().getId(), httpServletRequest, redirectAttributes);
    }
}
