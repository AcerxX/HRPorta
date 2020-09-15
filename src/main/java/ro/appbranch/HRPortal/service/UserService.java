package ro.appbranch.HRPortal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ro.appbranch.HRPortal.dto.user.SaveUserRequest;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.repository.CompanyRepository;
import ro.appbranch.HRPortal.repository.RoleRepository;
import ro.appbranch.HRPortal.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            CompanyRepository companyRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
    }

    public void register(User user) {
        this.save(user);
    }

    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User getLoggedUser() {
        User user = null;

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != "anonymousUser") {
            user = userRepository.findByEmailAndStatusTrue(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername());
        }

        return user;
    }

    public void saveUser(SaveUserRequest saveUserRequest) {
        var email = saveUserRequest.getEmail() + "@" + this.getLoggedUser().getCompany().getEmailDomain();

        var alreadyExistingUser = userRepository.findByEmail(email);

        if (alreadyExistingUser.isPresent()) {
            throw new RuntimeException("Exista deja un angajat cu email-ul " + email);
        }

        var user = new User()
                .setEmail(email)
                .setFullName(saveUserRequest.getFullName())
                .setHireDate(saveUserRequest.getHireDate())
                .setCompany(this.getLoggedUser().getCompany())
                .setResponsibleUser(
                        userRepository.findById(saveUserRequest.getResponsibleUser())
                                .orElseThrow(() -> new RuntimeException("Responsabilul selectat nu a fost gasit in baza de date! Contactati echipa de support."))
                )
                .setRole(
                        roleRepository.findById(saveUserRequest.getRole())
                                .orElseThrow(() -> new RuntimeException("Rolul selectat nu a fost gasit in baza de date! Contactati echipa de support."))
                );

        userRepository.save(user);
    }
}