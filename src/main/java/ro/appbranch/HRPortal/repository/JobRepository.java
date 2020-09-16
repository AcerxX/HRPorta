package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.Job;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends PagingAndSortingRepository<Job, Integer> {
    List<Job> findAllByOrderByNameAsc();

    Optional<Job> findByName(String name);
}
