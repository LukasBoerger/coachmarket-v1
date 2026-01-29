package de.coachkompass.backend.infrastructure.coachsport;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CoachSportCrudRepository extends JpaRepository<CoachSportEntity, UUID> {
    List<CoachSportEntity> findAllByCoachIdOrderByPriorityAsc(UUID coachId);
    void deleteAllByCoachId(UUID coachId);
}