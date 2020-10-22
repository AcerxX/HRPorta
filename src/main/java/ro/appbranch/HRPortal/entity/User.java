package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "user")
public class User {
    public static final String INITIAL_PASSWORD = "INITIAL_PASSWORD";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String password = INITIAL_PASSWORD;
    private String fullName;
    private String image;
    private boolean status = true;
    private LocalDate hireDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User responsibleUser;

    @OneToMany(mappedBy = "responsibleUser", fetch = FetchType.LAZY)
    private List<User> responsibleForUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserTimeOffInfo> userTimeOffInfoList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserTimeOffLog> userTimeOffLogs = new ArrayList<>();

    @OneToMany(mappedBy = "approvalUser", fetch = FetchType.LAZY)
    private List<UserTimeOffLog> responsibleForUserTimeOffs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Transient
    private String passwordConfirm;

    public String getHireDateAsString() {
        return this.hireDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public List<UserTimeOffLog> getUserTimeOffLogs() {
        return this.userTimeOffLogs
                .stream()
                .filter(userTimeOffLog -> !userTimeOffLog.getStatus().equals(UserTimeOffLog.STATUS_DELETED))
                .collect(Collectors.toList());
    }

    public List<UserTimeOffLog> getResponsibleForUserTimeOffs() {
        return this.responsibleForUserTimeOffs
                .stream()
                .filter(userTimeOffLog -> userTimeOffLog.getStatus().equals(UserTimeOffLog.STATUS_NOT_APPROVED))
                .collect(Collectors.toList());
    }
}
