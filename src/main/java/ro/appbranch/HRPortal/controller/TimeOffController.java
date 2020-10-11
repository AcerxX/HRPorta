package ro.appbranch.HRPortal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.appbranch.HRPortal.dto.BaseResponse;
import ro.appbranch.HRPortal.dto.timeOff.AddTimeOffRequest;
import ro.appbranch.HRPortal.service.TimeOffService;

@Controller
@RequestMapping("/time-off")
public class TimeOffController extends SecuredController {
    private final TimeOffService timeOffService;

    @Autowired
    public TimeOffController(TimeOffService timeOffService) {
        this.timeOffService = timeOffService;
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
}
