package de.coachkompass.backend.infrastructure.coachspezialisation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CoachSpecializationCrudRepository
        extends JpaRepository<CoachSpecializationEntity, CoachSpecializationId> {

    List<CoachSpecializationEntity> findAllById_CoachIdOrderByPriorityAsc(UUID coachId);
    void deleteAllById_CoachId(UUID coachId);
}
