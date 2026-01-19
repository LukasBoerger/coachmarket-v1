package de.coachkompass.backend.application.sport;

import de.coachkompass.backend.domain.sport.Sport;

public class SportDtoMapper {

    private SportDtoMapper() {}

    public static SportDto toDto(Sport c) {
        return new SportDto(
                c.getId(),
                c.getName(),
                c.getSlug()
        );
    }
}
