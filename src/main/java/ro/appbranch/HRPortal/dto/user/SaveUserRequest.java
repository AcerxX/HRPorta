package ro.appbranch.HRPortal.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Accessors(chain = true)
public class SaveUserRequest {
    private String email;
    private String fullName;
    private String image;
    private Integer responsibleUser;
    private Integer role;
    private String job;
    private String hireDate;

    public LocalDate getHireDate() {
        if (ObjectUtils.isEmpty(hireDate)) {
            return null;
        }

        return LocalDate.parse(this.hireDate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
