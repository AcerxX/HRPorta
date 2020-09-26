package ro.appbranch.HRPortal.controller.cron;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
@RequestMapping("/cron")
public class BaseCronController {
    private String cronLog;

    public String getCronLog() {
        return cronLog;
    }

    public void addLogToCronLog(String newLog) {
        cronLog += "<br>" + newLog;
    }

    public void resetLog() {
        this.cronLog = "";
    }
}
