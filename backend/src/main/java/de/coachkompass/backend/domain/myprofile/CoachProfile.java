package de.coachkompass.backend.domain.myprofile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CoachProfile(
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
        List<SocialLink> socialLinks
) {
    public record SocialLink(String platform, String url) {}
}