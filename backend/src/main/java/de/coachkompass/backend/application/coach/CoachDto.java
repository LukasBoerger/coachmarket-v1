package de.coachkompass.backend.application.coach;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CoachDto(
        UUID id,
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
        String avatarUrl,
        List<String> imageUrls,
        List<SportRefDto> sports,
        List<SpecializationRefDto> specializations,
        List<SocialLinkDto> socialLinks
) {
    public record SportRefDto(String slug, String name) {}
    public record SpecializationRefDto(String slug, String name) {}
}