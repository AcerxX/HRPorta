package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    User findByEmailAndStatusTrue(String email);

    List<User> findAllByStatusTrue();

    Optional<User> findByEmail(String email);
}
