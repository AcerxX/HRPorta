package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.Permission;

public interface PermissionRepository extends PagingAndSortingRepository<Permission, Integer> {
}
