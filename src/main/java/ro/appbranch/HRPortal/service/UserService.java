package ro.appbranch.HRPortal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ro.appbranch.HRPortal.dto.user.SaveUserRequest;
import ro.appbranch.HRPortal.entity.Job;
import ro.appbranch.HRPortal.entity.Team;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.repository.JobRepository;
import ro.appbranch.HRPortal.repository.RoleRepository;
import ro.appbranch.HRPortal.repository.TeamRepository;
import ro.appbranch.HRPortal.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;
    private final JobRepository jobRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            RoleRepository roleRepository,
            JobRepository jobRepository,
            TeamRepository teamRepository
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
        this.jobRepository = jobRepository;
        this.teamRepository = teamRepository;
    }

    public void register(User user) {
        this.save(user);
    }

    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User getLoggedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            throw new RuntimeException("Utilizatorul nu este logat!");
        }

        return userRepository.findByEmailAndStatusTrue(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername())
                .orElseThrow(() -> {
                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                    new SecurityContextLogoutHandler().logout(request, null, authentication);
                    SecurityContextHolder.getContext().setAuthentication(null);

                    throw new RuntimeException("Utilizatorul nu mai este activ in baza de date!");
                });
    }

    public void saveUser(SaveUserRequest saveUserRequest) {
        User user;
        if (!ObjectUtils.isEmpty(saveUserRequest.getId())) {
            user = userRepository.findById(saveUserRequest.getId())
                    .orElseThrow(() -> new RuntimeException("Angajatul cu id-ul " + saveUserRequest.getId() + " nu a fost gasit in baza de date!"));
        } else {
            user = new User();
        }

        var email = saveUserRequest.getEmail() + "@" + this.getLoggedUser().getCompany().getEmailDomain();
        var alreadyExistingUser = userRepository.findByEmail(email);

        if (alreadyExistingUser.isPresent() && !alreadyExistingUser.get().getId().equals(user.getId())) {
            throw new RuntimeException("Exista deja un angajat cu email-ul " + email);
        }

        user
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
                )
                .setJob(
                        jobRepository.findByName(saveUserRequest.getJob())
                                .orElseGet(() -> {
                                    var job = new Job()
                                            .setName(saveUserRequest.getJob());

                                    jobRepository.save(job);

                                    return job;
                                })
                )
                .setTeam(
                        teamRepository.findByName(saveUserRequest.getTeam())
                                .orElseGet(() -> {
                                    var team = new Team()
                                            .setName(saveUserRequest.getTeam());

                                    teamRepository.save(team);

                                    return team;
                                })
                );

        userRepository.save(user);
    }

    public void changeEmailDomainForUsers(List<User> userList, String newEmailDomain) {
        userList.forEach(user -> user.setEmail(user.getEmail().split("@")[0] + "@" + newEmailDomain));

        userRepository.saveAll(userList);
    }
}