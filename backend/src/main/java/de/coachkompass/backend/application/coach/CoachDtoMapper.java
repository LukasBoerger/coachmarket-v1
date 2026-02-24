package de.coachkompass.backend.application.coach;

import de.coachkompass.backend.domain.coach.Coach;

import java.util.List;

public final class CoachDtoMapper {

    private CoachDtoMapper() {}

    public static CoachDto toDto(
            Coach coach,
            String avatarUrl,
            List<String> imageUrls,
            List<SocialLinkDto> socialLinks
    ) {
        return new CoachDto(
                coach.getId(),
                coach.getDisplayName(),
                coach.getSlug(),
                coach.getBio(),
                coach.getWebsiteUrl(),
                coach.getCity(),
                coach.getRegion(),
                coach.getCountry(),
                coach.isRemoteAvailable(),
                coach.isInPersonAvailable(),
                coach.getPriceMin(),
                coach.getPriceMax(),
                coach.getPricingModel(),
                coach.getCurrency(),
                coach.getStatus().name(),
                avatarUrl,
                imageUrls,
                coach.getSports().stream()
                        .map(s -> new CoachDto.SportRefDto(s.slug(), s.name()))
                        .toList(),
                coach.getSpecializations().stream()
                        .map(s -> new CoachDto.SpecializationRefDto(s.slug(), s.name()))
                        .toList(),
                socialLinks
        );
    }
}