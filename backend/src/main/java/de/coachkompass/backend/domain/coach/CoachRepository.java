package de.coachkompass.backend.domain.coach;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoachRepository {
    List<Coach> findAllPublished();
    Optional<Coach> findBySlug(String slug);
    List<Coach> searchPublished(CoachSearchQuery query);
    List<String> findSportSlugsByCoachId(UUID coachId);
    List<String> findSpecializationSlugsByCoachId(UUID coachId);
}