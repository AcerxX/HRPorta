package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
}
