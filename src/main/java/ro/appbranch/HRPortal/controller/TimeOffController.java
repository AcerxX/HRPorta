package ro.appbranch.HRPortal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ro.appbranch.HRPortal.dto.BaseResponse;
import ro.appbranch.HRPortal.dto.timeOff.AddTimeOffRequest;
import ro.appbranch.HRPortal.entity.Role;
import ro.appbranch.HRPortal.entity.UserTimeOffLog;
import ro.appbranch.HRPortal.repository.UserTimeOffLogRepository;
import ro.appbranch.HRPortal.service.TimeOffService;

@Controller
@RequestMapping("/time-off")
public class TimeOffController extends SecuredController {
    private final TimeOffService timeOffService;
    private final UserTimeOffLogRepository userTimeOffLogRepository;

    @Autowired
    public TimeOffController(TimeOffService timeOffService, UserTimeOffLogRepository userTimeOffLogRepository) {
        this.timeOffService = timeOffService;
        this.userTimeOffLogRepository = userTimeOffLogRepository;
    }


    @ResponseBody()
    @PostMapping("/add")
    public BaseResponse addTimeOff(@RequestBody AddTimeOffRequest addTimeOffRequest) {
        addTimeOffRequest.setUserId(this.getLoggedUser().getId());

        timeOffService.addTimeOff(addTimeOffRequest);

        return new BaseResponse();
    }

    @ResponseBody()
    @DeleteMapping("/delete/{id}")
    public BaseResponse deleteTimeOff(@PathVariable Integer id) {
        timeOffService.deleteTimeOffLog(id);

        return new BaseResponse();
    }


    @GetMapping("/review-list")
    public String reviewUserTimeOffLogs(Model model) {
        if (this.getLoggedUser().getRole().getId().equals(Role.ROLE_ID_HR) || this.getLoggedUser().getRole().getId().equals(Role.ROLE_ID_ADMIN)) {
            model.addAttribute("userTimeOffLogs", userTimeOffLogRepository.findAllByStatusOrderByStartDate(UserTimeOffLog.STATUS_NOT_APPROVED));
        } else {
            model.addAttribute("userTimeOffLogs", this.getLoggedUser().getResponsibleForUserTimeOffs());
        }

        return "timeOff/reviewUserTimeOffLogList";
    }


    @GetMapping("/review/{id}")
    public String reviewUserTimeOffLog(Model model, @PathVariable Integer id) {
        var userTimeOffLog = userTimeOffLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cererea nu a fost gasita!"));

        var loggedUser = this.getLoggedUser();
        if (!userTimeOffLog.getUser().equals(loggedUser)
                && !userTimeOffLog.getApprovalUser().equals(loggedUser)
                && !loggedUser.getRole().getId().equals(Role.ROLE_ID_HR)
                && !loggedUser.getRole().getId().equals(Role.ROLE_ID_ADMIN)
        ) {
            throw new RuntimeException("Nu ai access la aceasta pagina! Contacteaza un membru HR sau administrator");
        }

        model.addAttribute("userTimeOffLog", userTimeOffLog);

        return "timeOff/reviewTimeOff";
    }

    @ResponseBody()
    @PostMapping("/approve/{id}")
    public BaseResponse approveUserTimeOffLog(@PathVariable Integer id) {
        var response = new BaseResponse();

        changeUserTimeOffLogStatus(id, UserTimeOffLog.STATUS_APPROVED);

        return response;
    }

    private void changeUserTimeOffLogStatus(Integer id, Integer status) {
        if (this.getLoggedUser().getRole().getLevel() < 5) {
            throw new RuntimeException("Nu aveti dreptul sa aprobati cereri de concediu!");
        }

        var userTimeOffLog = userTimeOffLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concediul cu id-ul " + id + " nu a fost gasit!"));


        userTimeOffLog.setStatus(status);
        userTimeOffLogRepository.save(userTimeOffLog);
    }

    @ResponseBody()
    @PostMapping("/decline/{id}")
    public BaseResponse declineUserTimeOffLog(@PathVariable Integer id) {
        var response = new BaseResponse();

        changeUserTimeOffLogStatus(id, UserTimeOffLog.STATUS_DECLINED);

        return response;
    }
}
