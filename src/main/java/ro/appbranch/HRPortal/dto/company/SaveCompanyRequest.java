package ro.appbranch.HRPortal.dto.company;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SaveCompanyRequest {
    private String emailDomain;
    private String name;
}
