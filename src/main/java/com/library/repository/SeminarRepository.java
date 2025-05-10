package com.library.repository;

import com.library.model.Seminar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface SeminarRepository extends JpaRepository<Seminar, Long> {

    List<Seminar> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Seminar> findByNameContainingIgnoreCase(String keyword);
}

