package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "company_rule")
public class CompanyRule {
    public static final int ACTION_ADD = 1;
    public static final int ACTION_SET = 2;

    public static final int EXECUTION_MOMENT_BEGINNING_OF_YEAR = 1;
    public static final int EXECUTION_MOMENT_BEGINNING_OF_MONTH = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    private TimeOff timeOff;

    private Integer action;
    private Double daysNumber;
    private Integer executionMoment;
    private boolean cumulateYears = false;
    private LocalDate lastExecutionDate;

    @ManyToMany()
    @JoinTable(
            name = "company_rule_user",
            joinColumns = @JoinColumn(name = "company_rule_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();
}
