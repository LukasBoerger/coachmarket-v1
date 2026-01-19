package de.coachkompass.backend.infrastructure.repositoryimpl;

import de.coachkompass.backend.domain.coach.Coach;
import de.coachkompass.backend.domain.coach.CoachRepository;

import java.util.List;
import java.util.Optional;

public class CoachRepositoryImpl implements CoachRepository {
    @Override
    public List<Coach> findAllPublished() {
        return List.of();
    }

    @Override
    public Optional<Coach> findBySlug(String slug) {
        return Optional.empty();
    }
}
