package de.coachkompass.backend.domain.coach;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Coach {

    private UUID id;
    private String displayName;
    private String slug;
    private String bio;
    private String websiteUrl;
    private String city;
    private String region;
    private String country;
    private boolean remoteAvailable;
    private boolean inPersonAvailable;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private String pricingModel;
    private String currency;
    private CoachStatus status;
    private List<SportRef> sports;
    private List<SpecializationRef> specializations;

    public boolean isPublished() {
        return status == CoachStatus.PUBLISHED;
    }

    public record SportRef(String slug, String name) {}
    public record SpecializationRef(String slug, String name) {}
}