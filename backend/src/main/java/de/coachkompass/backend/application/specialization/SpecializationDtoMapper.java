package de.coachkompass.backend.application.specialization;

import de.coachkompass.backend.domain.specialization.Specialization;

public final class SpecializationDtoMapper {
    private SpecializationDtoMapper() {}

    public static SpecializationDto toDto(Specialization s) {
        return new SpecializationDto(s.getId(), s.getName(), s.getSlug());
    }
}