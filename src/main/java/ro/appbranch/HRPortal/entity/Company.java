package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String emailDomain;
    private boolean status;

    private String logo;

    @ManyToOne(fetch = FetchType.LAZY)
    private License license;

    @OneToMany(mappedBy = "company")
    private List<User> users = new ArrayList<>();

    public List<User> getCEOUsers() {
        return this.users.stream()
                .filter(user -> ObjectUtils.isEmpty(user.getResponsibleUser()))
                .collect(Collectors.toList());
    }

    public List<User> getHRUsers() {
        return this.users.stream()
                .filter(user -> user.getRole().getId().equals(Role.ROLE_ID_HR))
                .collect(Collectors.toList());
    }
}
