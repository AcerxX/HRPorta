package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends PagingAndSortingRepository<Team, Integer> {
    List<Team> findAllByOrderByNameAsc();

    Optional<Team> findByName(String name);
}
