package de.coachkompass.backend.domain.coach;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class Coach {

    private final UUID id;
    private final String displayName;
    private final String slug;
    private final String bio;
    private final String websiteUrl;
    private final String city;
    private final boolean remoteAvailable;
    private final boolean inPersonAvailable;
    private final BigDecimal priceMin;
    private final BigDecimal priceMax;
    private final String currency;
    private final CoachStatus status;

    public boolean isPublished() {
        return status == CoachStatus.PUBLISHED;
    }
}
