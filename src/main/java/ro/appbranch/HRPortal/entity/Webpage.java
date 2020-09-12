package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "webpage")
public class Webpage {
    @Id
    private String code;

    private String displayedName;
    private String link;
    private String faIcon;
    private boolean status;
    private Integer roleLevel;
}
