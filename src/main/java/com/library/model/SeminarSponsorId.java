package com.library.model;

import java.io.Serializable;
import java.util.Objects;

public class SeminarSponsorId implements Serializable {
    private Long seminarId;
    private Long sponsorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeminarSponsorId)) return false;
        SeminarSponsorId that = (SeminarSponsorId) o;
        return Objects.equals(seminarId, that.seminarId) && Objects.equals(sponsorId, that.sponsorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seminarId, sponsorId);
    }
}