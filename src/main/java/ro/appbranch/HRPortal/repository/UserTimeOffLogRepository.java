package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.UserTimeOffLog;

public interface UserTimeOffLogRepository extends PagingAndSortingRepository<UserTimeOffLog, Integer> {
}
