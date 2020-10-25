package ro.appbranch.HRPortal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ro.appbranch.HRPortal.config.HRPTransactional;
import ro.appbranch.HRPortal.dto.timeOff.AddTimeOffRequest;
import ro.appbranch.HRPortal.entity.UserTimeOffLog;
import ro.appbranch.HRPortal.repository.*;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class TimeOffService {
    private final TimeOffRepository timeOffRepository;
    private final UserTimeOffInfoRepository userTimeOffInfoRepository;
    private final HolidayRepository holidayRepository;
    private final UserTimeOffLogRepository userTimeOffLogRepository;
    private final UserRepository userRepository;

    @Autowired
    public TimeOffService(TimeOffRepository timeOffRepository, UserTimeOffInfoRepository userTimeOffInfoRepository, HolidayRepository holidayRepository, UserTimeOffLogRepository userTimeOffLogRepository, UserRepository userRepository) {
        this.timeOffRepository = timeOffRepository;
        this.userTimeOffInfoRepository = userTimeOffInfoRepository;
        this.holidayRepository = holidayRepository;
        this.userTimeOffLogRepository = userTimeOffLogRepository;
        this.userRepository = userRepository;
    }

    @HRPTransactional
    public void addTimeOff(AddTimeOffRequest addTimeOffRequest) {
        var user = userRepository.findById(addTimeOffRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost gasit in baza de date!"));

        var timeOff = timeOffRepository.findById(addTimeOffRequest.getTimeOffType())
                .orElseThrow(() -> new RuntimeException("Tipul zilei este invalid!"));

        var numberOfHolidays = holidayRepository.findAllBySuggestionFalseAndDateBetween(addTimeOffRequest.getStartDate(), addTimeOffRequest.getEndDate())
                .stream()
                .filter(holiday -> !holiday.getDate().getDayOfWeek().equals(DayOfWeek.SATURDAY) && !holiday.getDate().getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .count();

        var numberOfWorkingDays = addTimeOffRequest.getStartDate()
                .datesUntil(
                        addTimeOffRequest.getEndDate()
                                .plusDays(1)
                )
                .filter(localDate -> !localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                .count()
                - numberOfHolidays;

        if (numberOfWorkingDays == 0) {
            throw new RuntimeException("Concediul selectat nu contine nicio zi lucratoare! Nu este nevoie de o cerere de concediu pentru aceasta situatie!");
        }

        var userTimeOffLog = new UserTimeOffLog()
                .setUser(user)
                .setTimeOff(timeOff)
                .setStartDate(addTimeOffRequest.getStartDate())
                .setEndDate(addTimeOffRequest.getEndDate())
                .setNumberOfDays((int) numberOfWorkingDays)
                .setApprovalUser(user.getResponsibleUser());

        userTimeOffLogRepository.save(userTimeOffLog);

        removeTimeOffDays(userTimeOffLog);
    }

    private void removeTimeOffDays(UserTimeOffLog userTimeOffLog) {
        var userTimeOffInfoOptional = userTimeOffInfoRepository.findFirstByUserAndTimeOff(userTimeOffLog.getUser(), userTimeOffLog.getTimeOff());

        if (userTimeOffInfoOptional.isPresent()) {
            var userTimeOffInfo = userTimeOffInfoOptional.get();
            userTimeOffInfo.addNumberOfDays(-userTimeOffLog.getNumberOfDays().doubleValue());

            userTimeOffInfoRepository.save(userTimeOffInfo);

            userTimeOffLog.setUserTimeOffInfo(userTimeOffInfo);
            userTimeOffLogRepository.save(userTimeOffLog);
        }
    }

    public void changeUserTimeOffLogStatus(Integer userTimeOffLogId, Integer status) {
        var userTimeOffLog = userTimeOffLogRepository.findById(userTimeOffLogId)
                .orElseThrow(() -> new RuntimeException("Cererea cu id-ul " + userTimeOffLogId + " nu a fost gasita!"));

        if (userTimeOffLog.getStatus().equals(UserTimeOffLog.STATUS_DELETED)) {
            throw new RuntimeException("Cererea a fost deja stearsa din sistem!");
        }

        userTimeOffLog.setStatus(status);
        userTimeOffLogRepository.save(userTimeOffLog);

        if (List.of(UserTimeOffLog.STATUS_DECLINED, UserTimeOffLog.STATUS_DELETED).contains(userTimeOffLog.getStatus())
                && !ObjectUtils.isEmpty(userTimeOffLog.getUserTimeOffInfo())
        ) {
            var userTimeOffInfo = userTimeOffLog.getUserTimeOffInfo();
            userTimeOffInfo.addNumberOfDays(userTimeOffLog.getNumberOfDays().doubleValue());
            userTimeOffInfoRepository.save(userTimeOffInfo);
        }
    }
}