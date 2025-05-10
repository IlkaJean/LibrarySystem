package com.library.repository;

import com.library.model.SeminarSponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeminarSponsorRepository extends JpaRepository<SeminarSponsor, Long> {
    List<SeminarSponsor> findBySeminarId(Long seminarId);
    List<SeminarSponsor> findBySponsorId(Long sponsorId);
}