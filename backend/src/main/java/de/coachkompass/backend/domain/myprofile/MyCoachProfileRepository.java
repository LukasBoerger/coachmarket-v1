package de.coachkompass.backend.domain.myprofile;

import de.coachkompass.backend.application.coach.SocialLinkDto;

import java.math.BigDecimal;
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
            String region,
            String country,
            boolean remoteAvailable,
            boolean inPersonAvailable,
            BigDecimal priceMin,
            BigDecimal priceMax,
            String pricingModel,
            String currency,
            String status,
            List<String> sportSlugs,
            List<String> specializationSlugs,
            List<SocialLinkDto> socialLinks
    ) {}
}