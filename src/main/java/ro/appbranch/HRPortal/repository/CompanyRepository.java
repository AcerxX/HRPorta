package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.Company;

public interface CompanyRepository extends PagingAndSortingRepository<Company, Integer> {
}
