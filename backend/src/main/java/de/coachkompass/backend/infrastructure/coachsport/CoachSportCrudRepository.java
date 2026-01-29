package de.coachkompass.backend.infrastructure.coachsport;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CoachSportCrudRepository
        extends JpaRepository<CoachSportEntity, CoachSportId> {

    List<CoachSportEntity> findAllById_CoachIdOrderByPriorityAsc(UUID coachId);
    void deleteAllById_CoachId(UUID coachId);
}
