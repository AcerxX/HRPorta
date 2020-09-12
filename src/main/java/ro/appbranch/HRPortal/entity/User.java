package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String image;
    private boolean status = true;

    @ManyToOne(targetEntity = Company.class)
    private Company company;

    @ManyToOne(targetEntity = Role.class)
    private Role role;

    @Transient
    private String passwordConfirm;
}
