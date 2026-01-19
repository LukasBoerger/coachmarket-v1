package de.coachkompass.backend.domain.sport;

import java.util.UUID;

import de.coachkompass.backend.infrastructure.entities.SportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportRepository extends JpaRepository<SportEntity, UUID> {}