package de.coachkompass.backend.application.myprofile;

import de.coachkompass.backend.application.coach.SocialLinkDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record MyCoachProfileDto(
        @NotBlank String displayName,
        String bio,
        String websiteUrl,
        String city,
        String region,
        String country,

        @NotNull Boolean remoteAvailable,
        @NotNull Boolean inPersonAvailable,

        BigDecimal priceMin,
        BigDecimal priceMax,
        String pricingModel,
        @NotBlank String currency,

        @NotNull List<String> sportSlugs,
        @NotNull List<String> specializationSlugs,
        List<SocialLinkDto> socialLinks,

        String status,
        String slug
) {}