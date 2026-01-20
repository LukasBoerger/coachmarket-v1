package de.coachkompass.backend.infrastructure.specialization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpecializationCrudRepository extends JpaRepository<SpecializationEntity, UUID> {
    Optional<SpecializationEntity> findBySlug(String slug);
}
