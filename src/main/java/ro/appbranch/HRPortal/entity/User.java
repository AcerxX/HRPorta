package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    private Job job;

    @Transient
    private String passwordConfirm;

    public String getHireDateAsString() {
        return this.hireDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
