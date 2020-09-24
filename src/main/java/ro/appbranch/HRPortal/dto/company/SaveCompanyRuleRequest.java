package ro.appbranch.HRPortal.dto.company;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class SaveCompanyRuleRequest {
    private String ruleId;
    private Integer timeOffId;
    private Integer action;
    private Double daysNumber;
    private Integer executionMoment;
    private boolean cumulateYears;
    private List<Integer> userIds;
}
