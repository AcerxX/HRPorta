package ro.appbranch.HRPortal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.appbranch.HRPortal.config.HRPTransactional;
import ro.appbranch.HRPortal.dto.timeOff.AddTimeOffRequest;
import ro.appbranch.HRPortal.entity.TimeOff;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.entity.UserTimeOffLog;
import ro.appbranch.HRPortal.repository.*;

import java.time.DayOfWeek;

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
            throw new RuntimeException("Durata concediului selectat este de 0 zile!");
        }

        var userTimeOffLog = new UserTimeOffLog()
                .setUser(user)
                .setTimeOff(timeOff)
                .setStartDate(addTimeOffRequest.getStartDate())
                .setEndDate(addTimeOffRequest.getEndDate())
                .setNumberOfDays((int) numberOfWorkingDays)
                .setApprovalUser(user.getResponsibleUser());

        userTimeOffLogRepository.save(userTimeOffLog);

        removeTimeOffDays(user, timeOff, (double) numberOfWorkingDays);
    }

    private void removeTimeOffDays(User user, TimeOff timeOff, Double workingDays) {
        var userTimeOffOptional = userTimeOffInfoRepository.findFirstByUserAndTimeOff(user, timeOff);

        if (userTimeOffOptional.isPresent()) {
            var userTimeOff = userTimeOffOptional.get();
            userTimeOff.addNumberOfDays(-workingDays);

            userTimeOffInfoRepository.save(userTimeOff);
        }
    }
}