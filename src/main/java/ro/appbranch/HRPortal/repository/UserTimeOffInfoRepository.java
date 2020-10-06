package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.TimeOff;
import ro.appbranch.HRPortal.entity.User;
import ro.appbranch.HRPortal.entity.UserTimeOffInfo;

import java.util.Optional;

public interface UserTimeOffInfoRepository extends PagingAndSortingRepository<UserTimeOffInfo, Integer> {
    Optional<UserTimeOffInfo> findFirstByUserAndTimeOff(User user, TimeOff timeOff);
}
