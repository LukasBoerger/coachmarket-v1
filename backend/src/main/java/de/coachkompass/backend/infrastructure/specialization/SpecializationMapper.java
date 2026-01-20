package de.coachkompass.backend.infrastructure.specialization;

import de.coachkompass.backend.domain.specialization.Specialization;

public final class SpecializationMapper {
    private SpecializationMapper() {}

    public static Specialization toDomain(SpecializationEntity e) {
        return new Specialization(e.getId(), e.getName(), e.getSlug());
    }
}