package de.coachkompass.backend.domain.specialization;

import java.util.List;
import java.util.Optional;

public interface SpecializationRepository {
    List<Specialization> findAll();
    Optional<Specialization> getBySlug(String slug);
}
