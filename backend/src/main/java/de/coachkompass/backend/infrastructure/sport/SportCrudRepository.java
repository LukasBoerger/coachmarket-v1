package de.coachkompass.backend.infrastructure.sport;

import de.coachkompass.backend.application.sport.SportDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SportCrudRepository extends JpaRepository<SportEntity, UUID> {
    Optional<SportEntity> findBySlug(String slug);
}

