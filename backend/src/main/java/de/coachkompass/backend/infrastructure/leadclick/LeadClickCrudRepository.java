package de.coachkompass.backend.infrastructure.leadclick;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface LeadClickCrudRepository extends JpaRepository<LeadClickEntity, UUID> {

    @Query("select count(l) from LeadClickEntity l where l.coachId = :coachId and l.type = :type")
    long countByCoachIdAndType(@Param("coachId") UUID coachId, @Param("type") String type);
}