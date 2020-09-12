package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.Role;

public interface RoleRepository extends PagingAndSortingRepository<Role, Integer> {
}
