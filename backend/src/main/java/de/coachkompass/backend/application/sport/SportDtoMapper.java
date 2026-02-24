package de.coachkompass.backend.application.sport;

import de.coachkompass.backend.domain.sport.Sport;

public final class SportDtoMapper {
    private SportDtoMapper() {}

    public static SportDto toDto(Sport s) {
        return new SportDto(s.getId(), s.getName(), s.getSlug());
    }
}