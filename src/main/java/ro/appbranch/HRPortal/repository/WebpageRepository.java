package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.Webpage;

import java.util.List;

public interface WebpageRepository extends PagingAndSortingRepository<Webpage, Integer> {
    List<Webpage> findAllByRoleLevelIsLessThanEqual(Integer roleLevel);
}
