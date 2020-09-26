package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.CompanyRule;

import java.util.List;

public interface CompanyRuleRepository extends PagingAndSortingRepository<CompanyRule, Integer> {
    List<CompanyRule> findAllByExecutionMoment(Integer executionMoment);
}
