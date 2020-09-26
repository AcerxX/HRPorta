package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

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

    public UserTimeOffInfo addNumberOfDays(Double numberOfDays) {
        this.currentNumberOfDays += numberOfDays;
        return this;
    }
}
