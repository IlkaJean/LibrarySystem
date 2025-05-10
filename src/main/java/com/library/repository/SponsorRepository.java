package com.library.repository;

import com.library.model.Sponsor;
import com.library.model.Individual;
import com.library.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {

    // Fetch all individual sponsors
    @Query("SELECT i FROM Individual i")
    List<Individual> findAllIndividuals();

    // Fetch all organization sponsors
    @Query("SELECT o FROM Organization o")
    List<Organization> findAllOrganizations();

    // Optional: filter by type
    @Query("SELECT s FROM Sponsor s WHERE s.sponsorType = :type")
    List<Sponsor> findBySponsorType(String type);
}
