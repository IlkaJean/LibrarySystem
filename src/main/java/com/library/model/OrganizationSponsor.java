package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PJI_ORG")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationSponsor extends Sponsor {

    @Column(name = "ORG_NAME", nullable = false)
    private String organizationName;

    // Constructor that sets sponsor type
    public OrganizationSponsor(Long id, String organizationName) {
        super(id, "O", null);
        this.organizationName = organizationName;
    }
}