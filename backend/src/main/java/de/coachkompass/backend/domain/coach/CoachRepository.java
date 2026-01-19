package de.coachkompass.backend.domain.coach;

import java.util.List;
import java.util.Optional;

public interface CoachRepository {
    List<Coach> findAllPublished();
    Optional<Coach> findBySlug(String slug);
}
