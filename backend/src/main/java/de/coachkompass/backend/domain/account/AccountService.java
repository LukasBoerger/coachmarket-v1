package de.coachkompass.backend.domain.account;

import de.coachkompass.backend.infrastructure.account.AccountCrudRepository;
import de.coachkompass.backend.infrastructure.account.AccountEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountCrudRepository repo;

    public AccountService(AccountCrudRepository repo) {
        this.repo = repo;
    }

    public AccountEntity findOrCreateCoachAccount(String firebaseUid) {
        var now = OffsetDateTime.now();

        return repo.findByFirebaseUid(firebaseUid)
                .map(existing -> {
                    existing.setUpdatedAt(now);
                    existing.setLastLoginAt(now);
                    return repo.save(existing);
                })
                .orElseGet(() -> repo.save(AccountEntity.builder()
                        .id(UUID.randomUUID())
                        .firebaseUid(firebaseUid)
                        .role("COACH")
                        .createdAt(now)
                        .updatedAt(now)
                        .lastLoginAt(now)
                        .build()));
    }
}
