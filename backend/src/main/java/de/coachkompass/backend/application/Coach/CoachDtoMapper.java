package de.coachkompass.backend.application.Coach;

import de.coachkompass.backend.domain.coach.Coach;

public final class CoachDtoMapper {
    private CoachDtoMapper() {}

    public static CoachDto toDto(Coach c) {
        return new CoachDto(
                c.getId(),
                c.getDisplayName(),
                c.getSlug(),
                c.getBio(),
                c.getWebsiteUrl(),
                c.getCity(),
                c.isRemoteAvailable(),
                c.isInPersonAvailable(),
                c.getPriceMin(),
                c.getPriceMax(),
                c.getCurrency(),
                c.getStatus().name()
        );
    }
}