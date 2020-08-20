package ro.appbranch.HRPortal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void register(User user) {
        this.save(user);
    }

    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User login(String username, String password) {
        var user = userRepository.findByUsernameAndStatusTrue(username);
        Assert.notNull(user, "No active user was found for username " + username);

        if (!this.bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password for user " + username);
        }

        return user;
    }

    public User getLoggedUser() {
        User user = null;

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != "anonymousUser") {
            user = userRepository.findByUsernameAndStatusTrue(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername());
        }

        return user;
    }
}