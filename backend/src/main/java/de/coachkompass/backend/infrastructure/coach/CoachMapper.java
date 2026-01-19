package de.coachkompass.backend.infrastructure.coach;

import de.coachkompass.backend.domain.coach.Coach;
import de.coachkompass.backend.domain.coach.CoachStatus;

public final class CoachMapper {
    private CoachMapper() {}

    public static Coach toDomain(CoachEntity e) {
        return new Coach(
                e.getId(),
                e.getDisplayName(),
                e.getSlug(),
                e.getBio(),
                e.getWebsiteUrl(),
                e.getCity(),
                e.isRemoteAvailable(),
                e.isInPersonAvailable(),
                e.getPriceMin(),
                e.getPriceMax(),
                e.getCurrency(),
                CoachStatus.valueOf(e.getStatus())
        );
    }
}