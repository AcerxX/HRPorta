package ro.appbranch.HRPortal.dto.timeOff;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Accessors(chain = true)
public class AddTimeOffRequest {
    private Integer userId;
    private Integer timeOffType;
    private String startDate;
    private String endDate;

    public LocalDate getStartDate() {
        if (ObjectUtils.isEmpty(startDate)) {
            return null;
        }

        return LocalDate.parse(this.startDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public LocalDate getEndDate() {
        if (ObjectUtils.isEmpty(endDate)) {
            return null;
        }

        return LocalDate.parse(this.endDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
