package com.library.repository;

import com.library.model.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    List<Exhibition> findByExpensesGreaterThanEqual(Double minExpenses);

    List<Exhibition> findByStartDateTimeAfter(LocalDateTime date);

    List<Exhibition> findByNameContainingIgnoreCase(String keyword);
}

