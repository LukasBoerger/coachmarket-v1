package de.coachkompass.backend.infrastructure.coach;

import java.math.BigDecimal;
import java.util.UUID;

public interface CoachRow {
    UUID getId();
    String getDisplayName();
    String getSlug();
    String getBio();
    String getWebsiteUrl();
    String getCity();
    Boolean getRemoteAvailable();
    Boolean getInPersonAvailable();
    BigDecimal getPriceMin();
    BigDecimal getPriceMax();
    String getCurrency();
    String getStatus();
}