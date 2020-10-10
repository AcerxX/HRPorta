package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Month;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "user_time_off_info")
public class UserTimeOffInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private TimeOff timeOff;

    private Double currentNumberOfDays = 0.0;
    private Double currentMaxNumberOfDays = 0.0;
    private Double lastPeriodNumberOfDays = 0.0;

    public UserTimeOffInfo addNumberOfDays(Double numberOfDays) {
        this.currentNumberOfDays += numberOfDays;

        if (LocalDate.now().getMonth().equals(Month.JANUARY)) {
            this.currentMaxNumberOfDays = 0.0;
        }
        this.currentMaxNumberOfDays += numberOfDays;

        return this;
    }
}
