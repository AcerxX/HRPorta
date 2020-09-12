package ro.appbranch.HRPortal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.entity.Webpage;
import ro.appbranch.HRPortal.repository.WebpageRepository;
import ro.appbranch.HRPortal.service.UserService;

import java.util.List;


@Controller
public class SecuredController {
    @Autowired // This one stays here, not above a constructor
    private UserService userService;
    @Autowired // This one stays here, not above a constructor
    private WebpageRepository webpageRepository;

    @ModelAttribute("assetVersion")
    public String getAssetVersion() {
        return "?v1";
    }

    @ModelAttribute("user")
    public User getLoggedUser() {
        return userService.getLoggedUser();
    }

    @ModelAttribute("webpages")
    public List<Webpage> getCurrentUserWebPages() {
        var loggedUser = getLoggedUser();

        return loggedUser == null ? null : webpageRepository.findAllByRoleLevelIsLessThanEqual(loggedUser.getRole().getLevel());
    }

    @ModelAttribute("currentUri")
    public String getCurrentUri() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI();
    }
}
