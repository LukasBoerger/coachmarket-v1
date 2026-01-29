package de.coachkompass.backend.infrastructure.coachspezialisation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CoachSpecializationCrudRepository extends JpaRepository<CoachSpecializationEntity, UUID> {
    List<CoachSpecializationEntity> findAllByCoachIdOrderByPriorityAsc(UUID coachId);
    void deleteAllByCoachId(UUID coachId);
}