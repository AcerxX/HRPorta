package ro.appbranch.HRPortal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.appbranch.HRPortal.dto.BaseResponse;
import ro.appbranch.HRPortal.dto.user.SaveUserRequest;
import ro.appbranch.HRPortal.repository.UserRepository;
import ro.appbranch.HRPortal.service.UserService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController extends SecuredController {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your email or password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @GetMapping("/user/list")
    public String userList(Model model) {
        model.addAttribute("allUsers", userRepository.findAllByStatusTrue());

        return "user/userList";
    }

    @GetMapping("/user/add")
    public String addUserInterface(Model model) {
        model.addAttribute("allUsers", userRepository.findAllByStatusTrue());

        return "user/addUser";
    }

    @ResponseBody()
    @PostMapping("/user/update")
    public BaseResponse saveUser(@RequestBody SaveUserRequest saveUserRequest) {
        var response = new BaseResponse();

        userService.saveUser(saveUserRequest);

        return response;
    }


    @GetMapping("/user/info/{id}")
    public String userInfo(Model model, @PathVariable Integer id, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {
        var user = userRepository.findById(id);

        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE_FLASH_KEY, "Utilizatorul cu id-ul " + id + " nu a fost gasit in baza de date!");

            var lastPage = httpServletRequest.getHeader("Referer");

            if (ObjectUtils.isEmpty(lastPage)) {
                lastPage = "/";
            }

            return "redirect:" + lastPage;
        }

        model.addAttribute("allUsers", userRepository.findAllByStatusTrue());

        return "user/userInfo";
    }
}
