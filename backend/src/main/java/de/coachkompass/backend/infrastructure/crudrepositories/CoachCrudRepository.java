package de.coachkompass.backend.infrastructure.crudrepositories;

import de.coachkompass.backend.domain.coach.Coach;
import de.coachkompass.backend.infrastructure.entities.CoachEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoachCrudRepository extends JpaRepository<CoachEntity, UUID> {
    Optional<CoachEntity> findBySlug(String slug);
    List<CoachEntity> findByStatus(String status);
}
