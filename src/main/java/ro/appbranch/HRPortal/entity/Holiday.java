package ro.appbranch.HRPortal.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "holiday")
public class Holiday {
    @Id
    private LocalDate date;
    private String name;
    private boolean isSuggestion = false;
}
