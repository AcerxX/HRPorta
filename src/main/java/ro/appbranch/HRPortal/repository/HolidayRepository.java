package ro.appbranch.HRPortal.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ro.appbranch.HRPortal.entity.Holiday;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends PagingAndSortingRepository<Holiday, LocalDate> {
    List<Holiday> findAllBySuggestionFalseAndDateBetween(LocalDate start, LocalDate end);
}
