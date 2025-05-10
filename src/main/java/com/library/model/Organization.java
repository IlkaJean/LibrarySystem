package com.library.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PJI_ORG")
@PrimaryKeyJoinColumn(name = "SPONSOR_ID")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("O")
public class Organization extends Sponsor {
    @Column(name = "ORG_NAME")
    private String orgName;

    public Organization(Long id, String orgName) {
        super(id, "O", null);
        this.orgName = orgName;
    }
}

