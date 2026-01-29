package de.coachkompass.backend.domain.myprofile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MyCoachProfileRepository {

    Optional<CoachProfileAggregate> findByAccountId(UUID accountId);
    void setStatus(UUID coachId, String status);

    CoachProfileAggregate upsert(UUID accountId, CoachProfileAggregate aggregate);

    record CoachProfileAggregate(
            UUID coachId,
            String displayName,
            String slug,
            String bio,
            String websiteUrl,
            String city,
            boolean remoteAvailable,
            boolean inPersonAvailable,
            java.math.BigDecimal priceMin,
            java.math.BigDecimal priceMax,
            String currency,
            String status,
            List<String> sportSlugs,
            List<String> specializationSlugs
    ) {}
}
