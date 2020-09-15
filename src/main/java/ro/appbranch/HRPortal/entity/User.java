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

    @ManyToOne()
    private User responsibleUser;

    @ManyToOne(targetEntity = Company.class)
    private Company company;

    @ManyToOne(targetEntity = Role.class)
    private Role role;

    @Transient
    private String passwordConfirm;
}
