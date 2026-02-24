package de.coachkompass.backend.infrastructure.media;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MediaAssetJpaRepository extends JpaRepository<MediaAssetEntity, UUID> {

    List<MediaAssetEntity> findByCoachIdAndTypeAndVisibilityOrderByCreatedAtAsc(
            UUID coachId, String type, String visibility
    );

    List<MediaAssetEntity> findByCoachIdInAndTypeAndVisibility(
            List<UUID> coachIds, String type, String visibility
    );
}