package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "webpage")
public class Webpage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private String displayedName;
    private String link;
    private String faIcon;
    private boolean status;
    private Integer roleLevel;
    private Integer licenseLevel;
    private boolean newWebpage = false;

    @ManyToOne(targetEntity = Webpage.class)
    private Webpage parent;

    @Transient
    private List<Webpage> children = new ArrayList<>();

    @Transient
    private boolean active = false;

    @Transient
    private boolean disabled = false;
}
