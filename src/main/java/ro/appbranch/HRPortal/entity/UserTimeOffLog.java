package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private UserTimeOffInfo userTimeOffInfo;

    private Integer numberOfDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status = STATUS_NOT_APPROVED;

    @ManyToOne(fetch = FetchType.LAZY)
    private User approvalUser;
    private LocalDate approvalDate;

    private LocalDate created = LocalDate.now();

    @Transient
    private Integer numberOfOverlappingDays;

    public String getStartDateAsString() {
        return this.startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getEndDateAsString() {
        return this.endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getCreatedAsString() {
        return this.created.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getApprovalDateAsString() {
        if (ObjectUtils.isEmpty(this.approvalDate)) {
            return "-";
        }

        return this.approvalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getStatusAsString() {
        return switch (this.status) {
            case STATUS_DECLINED -> "REFUZAT";
            case STATUS_DELETED -> "STERS";
            case STATUS_NOT_APPROVED -> "NEAPROBAT";
            case STATUS_APPROVED -> "APROBAT";
            default -> "N/A";
        };
    }

    public String getCreatedAsTimeAgoString() {
        var daysAgo = this.getCreated().until(LocalDate.now()).getDays();
        return switch (daysAgo) {
            case 0 -> "Azi";
            case 1 -> "Acum o zi";
            default -> "Acum " + daysAgo + " zile";
        };
    }

    public List<UserTimeOffLog> getColleaguesUserTimeOffLogsInThisInterval() {
        return this.user
                .getColleagues()
                .stream()
                .flatMap(user1 ->
                        user1.getUserTimeOffLogs()
                                .stream()
                                .filter(userTimeOffLog -> {
                                    var maxStartDate = userTimeOffLog.getStartDate().isBefore(this.getStartDate()) ? this.getStartDate() : userTimeOffLog.getStartDate();
                                    var minEndDate = userTimeOffLog.getEndDate().isBefore(this.getEndDate()) ? userTimeOffLog.getEndDate() : this.getStartDate();

                                    userTimeOffLog.setNumberOfOverlappingDays(maxStartDate.until(minEndDate).getDays());

                                    return userTimeOffLog.getNumberOfOverlappingDays() > 0;
                                })
                ).collect(Collectors.toList());
    }
}
