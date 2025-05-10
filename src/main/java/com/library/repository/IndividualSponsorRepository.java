package com.library.repository;

import com.library.model.IndividualSponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndividualSponsorRepository extends JpaRepository<IndividualSponsor, Long> {
    List<IndividualSponsor> findByLastNameContainingIgnoreCase(String lastName);
}