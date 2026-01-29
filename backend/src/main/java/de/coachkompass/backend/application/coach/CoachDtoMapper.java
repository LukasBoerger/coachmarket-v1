package de.coachkompass.backend.application.coach;

import de.coachkompass.backend.domain.coach.Coach;

public final class CoachDtoMapper {

    private CoachDtoMapper() {}

    public static CoachDto toDto(Coach coach, String avatarUrl, java.util.List<String> imageUrls) {
        return new CoachDto(
                coach.getId(),
                coach.getDisplayName(),
                coach.getSlug(),
                coach.getBio(),
                coach.getWebsiteUrl(),
                coach.getCity(),
                coach.isRemoteAvailable(),
                coach.isInPersonAvailable(),
                coach.getPriceMin(),
                coach.getPriceMax(),
                coach.getCurrency(),
                coach.getStatus().name(),
                avatarUrl,
                imageUrls
        );
    }
}
