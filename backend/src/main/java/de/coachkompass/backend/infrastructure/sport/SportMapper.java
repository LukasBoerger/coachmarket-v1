package de.coachkompass.backend.infrastructure.sport;

import de.coachkompass.backend.domain.coach.Coach;
import de.coachkompass.backend.domain.coach.CoachStatus;
import de.coachkompass.backend.domain.sport.Sport;
import de.coachkompass.backend.infrastructure.coach.CoachEntity;

public final class SportMapper {
    private SportMapper() {}

    public static Sport toDomain(SportEntity e) {
        return Sport.builder().id(e.getId()).name(e.getName()).slug(e.getSlug()).build();
    }
}