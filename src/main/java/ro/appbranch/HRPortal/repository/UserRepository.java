package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    User findByUsernameAndStatusTrue(String username);
}
