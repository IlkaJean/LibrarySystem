package com.library.repository;

import com.library.model.SeminarAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeminarAttendanceRepository extends JpaRepository<SeminarAttendance, Long> {
    List<SeminarAttendance> findBySeminarId(Long seminarId);
    List<SeminarAttendance> findByAuthorId(Long authorId);
}