package de.coachkompass.backend.infrastructure.socialmedia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SocialMediaLinkCrudRepository extends JpaRepository<SocialMediaLinkEntity, UUID> {
    List<SocialMediaLinkEntity> findAllByCoachIdOrderByDisplayOrderAsc(UUID coachId);
    void deleteAllByCoachId(UUID coachId);
    List<SocialMediaLinkEntity> findAllByCoachIdIn(List<UUID> coachIds);
}