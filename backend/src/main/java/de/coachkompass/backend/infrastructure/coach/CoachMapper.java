package de.coachkompass.backend.infrastructure.coach;

import de.coachkompass.backend.domain.coach.Coach;
import de.coachkompass.backend.domain.coach.CoachStatus;

import java.util.List;

public final class CoachMapper {
    private CoachMapper() {}

    public static Coach toDomain(CoachEntity e, List<String> sportSlugs, List<String> specializationSlugs) {
        return Coach.builder()
                .id(e.getId())
                .displayName(e.getDisplayName())
                .slug(e.getSlug())
                .bio(e.getBio())
                .websiteUrl(e.getWebsiteUrl())
                .city(e.getCity())
                .region(e.getRegion())
                .country(e.getCountry())
                .remoteAvailable(e.isRemoteAvailable())
                .inPersonAvailable(e.isInPersonAvailable())
                .priceMin(e.getPriceMin())
                .priceMax(e.getPriceMax())
                .pricingModel(e.getPricingModel())
                .currency(e.getCurrency())
                .status(CoachStatus.valueOf(e.getStatus()))
                .sportSlugs(sportSlugs)
                .specializationSlugs(specializationSlugs)
                .build();
    }

    public static Coach toDomain(CoachRow r, List<String> sportSlugs, List<String> specializationSlugs) {
        return Coach.builder()
                .id(r.getId())
                .displayName(r.getDisplayName())
                .slug(r.getSlug())
                .bio(r.getBio())
                .websiteUrl(r.getWebsiteUrl())
                .city(r.getCity())
                .region(r.getRegion())
                .country(r.getCountry())
                .remoteAvailable(Boolean.TRUE.equals(r.getRemoteAvailable()))
                .inPersonAvailable(Boolean.TRUE.equals(r.getInPersonAvailable()))
                .priceMin(r.getPriceMin())
                .priceMax(r.getPriceMax())
                .pricingModel(r.getPricingModel())
                .currency(r.getCurrency())
                .status(CoachStatus.valueOf(r.getStatus()))
                .sportSlugs(sportSlugs)
                .specializationSlugs(specializationSlugs)
                .build();
    }
}