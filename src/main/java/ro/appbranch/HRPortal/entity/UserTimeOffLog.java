package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "user_time_off_log")
public class UserTimeOffLog {
    public static final int STATUS_DECLINED = -1;
    public static final int STATUS_DELETED = 0;
    public static final int STATUS_NOT_APPROVED = 1;
    public static final int STATUS_APPROVED = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private TimeOff timeOff;

    private Double numberOfDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status = STATUS_NOT_APPROVED;

    @ManyToOne(fetch = FetchType.LAZY)
    private User approvalUser;
    private LocalDate approvalDate;

    private LocalDate created = LocalDate.now();
}
