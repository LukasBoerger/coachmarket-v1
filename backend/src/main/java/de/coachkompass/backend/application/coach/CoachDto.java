package de.coachkompass.backend.application.coach;

import java.math.BigDecimal;
import java.util.UUID;

public record CoachDto(
        UUID id,
        String displayName,
        String slug,
        String bio,
        String websiteUrl,
        String city,
        boolean remoteAvailable,
        boolean inPersonAvailable,
        BigDecimal priceMin,
        BigDecimal priceMax,
        String currency,
        String status
) {}