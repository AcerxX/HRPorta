package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.TimeOff;

import java.util.List;
import java.util.Optional;

public interface TimeOffRepository extends PagingAndSortingRepository<TimeOff, Integer> {
    List<TimeOff> findAllByStatusTrueOrderBySortOrderAsc();

    Optional<TimeOff> findByStatusTrueAndId(Integer id);
}
