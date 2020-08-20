package ro.appbranch.HRPortal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.service.UserService;


@Controller
public class SecuredController {
    @Autowired // This one stays here, not above a constructor
    private UserService userService;

    @ModelAttribute("assetVersion")
    public String getAssetVersion() {
        return "?v1";
    }

    @ModelAttribute("user")
    public User getLoggedUser() {
        return userService.getLoggedUser();
    }
}
