package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "permission")
@Getter
@Setter
@Accessors(chain = true)
public class Permission {
    public static final int PERMISSION_ID_ADMIN = 13;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String code;
    private String group;
}
