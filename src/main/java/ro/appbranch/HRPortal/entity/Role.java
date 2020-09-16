package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "role")
@Getter
@Setter
@Accessors(chain = true)
public class Role {
    public static final int ROLE_ID_USER = 1;
    public static final int ROLE_ID_TEAM_LEADER = 2;
    public static final int ROLE_ID_HR = 3;
    public static final int ROLE_ID_ADMIN = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;
    private String name;
    private String group;
    private Integer level;
}
