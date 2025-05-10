package com.library.repository;

import com.library.model.OrganizationSponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationSponsorRepository extends JpaRepository<OrganizationSponsor, Long> {
    Optional<OrganizationSponsor> findByOrganizationNameIgnoreCase(String organizationName);

}