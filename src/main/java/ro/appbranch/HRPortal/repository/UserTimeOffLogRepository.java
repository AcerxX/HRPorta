package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.UserTimeOffLog;

import java.util.List;

public interface UserTimeOffLogRepository extends PagingAndSortingRepository<UserTimeOffLog, Integer> {
    List<UserTimeOffLog> findAllByStatusOrderByStartDate(Integer status);
}
