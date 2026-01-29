package de.coachkompass.backend.application.myprofile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record MyCoachProfileDto(
        @NotBlank String displayName,
        String bio,
        String websiteUrl,
        String city,

        @NotNull Boolean remoteAvailable,
        @NotNull Boolean inPersonAvailable,

        BigDecimal priceMin,
        BigDecimal priceMax,
        @NotBlank String currency,

        @NotNull List<String> sportSlugs,
        @NotNull List<String> specializationSlugs,

        String status,
        String slug
) {}
