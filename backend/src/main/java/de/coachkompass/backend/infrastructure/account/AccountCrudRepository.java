package de.coachkompass.backend.infrastructure.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountCrudRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByFirebaseUid(String firebaseUid);
}
