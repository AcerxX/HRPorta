package ro.appbranch.HRPortal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ro.appbranch.HRPortal.dto.BaseResponse;
import ro.appbranch.HRPortal.dto.user.SaveUserRequest;
import ro.appbranch.HRPortal.repository.UserRepository;
import ro.appbranch.HRPortal.service.UserService;

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
}
