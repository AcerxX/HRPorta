package ro.appbranch.HRPortal.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
public class BaseResponse {
    private Integer status = 200;
    private String message = "Success";
    private Date timestamp = new Date();
    private String redirect;
}
