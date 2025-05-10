package com.library.repository;

import com.library.model.ExhibitionAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExhibitionAttendanceRepository extends JpaRepository<ExhibitionAttendance, Long> {
    List<ExhibitionAttendance> findByExhibitionId(Long exhibitionId);
    List<ExhibitionAttendance> findByCustomerId(Long customerId);
}