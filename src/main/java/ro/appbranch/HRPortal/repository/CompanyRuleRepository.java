package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.CompanyRule;

public interface CompanyRuleRepository extends PagingAndSortingRepository<CompanyRule, Integer> {
}
