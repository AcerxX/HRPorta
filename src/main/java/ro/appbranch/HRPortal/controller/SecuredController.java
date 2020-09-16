package ro.appbranch.HRPortal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.entity.Webpage;
import ro.appbranch.HRPortal.repository.WebpageRepository;
import ro.appbranch.HRPortal.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class SecuredController {
    public static final String ERROR_MESSAGE_FLASH_KEY = "redirectErrorMessage";

    @Autowired // This one stays here, not above a constructor
    private UserService userService;
    @Autowired // This one stays here, not above a constructor
    private WebpageRepository webpageRepository;

    @ModelAttribute("assetVersion")
    public String getAssetVersion() {
        return "?v1";
    }

    @ModelAttribute("loggedUser")
    public User getLoggedUser() {
        return userService.getLoggedUser();
    }

    @ModelAttribute("currentUri")
    public static String getCurrentUri() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI();
    }

    @ModelAttribute("webpages")
    public List<Webpage> getCurrentUserWebPages() {
        var loggedUser = getLoggedUser();

        if (ObjectUtils.isEmpty(loggedUser)) {
            return List.of();
        }

        // compute children
        Map<Integer, Webpage> webpageMap = new HashMap<>();

        var allWebPages = webpageRepository.findAllByRoleLevelIsLessThanEqual(loggedUser.getRole().getLevel());

        allWebPages.stream()
                .filter(webpage -> ObjectUtils.isEmpty(webpage.getParent()))
                .forEach(webpage -> {
                    if (getCurrentUri().equals(webpage.getLink())) {
                        webpage.setActive(true);
                    }

                    if (loggedUser.getCompany().getLicense().getLevel() < webpage.getLicenseLevel()) {
                        webpage.setDisabled(true);
                    }

                    webpageMap.put(webpage.getId(), webpage);
                });

        allWebPages.stream()
                .filter(webpage -> !ObjectUtils.isEmpty(webpage.getParent()))
                .forEach(webpage -> {
                    // if parent is disabled do not compute children
                    if (webpageMap.get(webpage.getParent().getId()).isDisabled()) {
                        return;
                    }

                    if (getCurrentUri().equals(webpage.getLink())) {
                        webpage.setActive(true);
                        webpageMap.get(webpage.getParent().getId()).setActive(true);
                    }

                    if (loggedUser.getCompany().getLicense().getLevel() < webpage.getLicenseLevel()) {
                        webpage.setDisabled(true);
                    }

                    webpageMap.get(webpage.getParent().getId()).getChildren().add(webpage);
                });


        return new ArrayList<>(webpageMap.values());
    }
}
