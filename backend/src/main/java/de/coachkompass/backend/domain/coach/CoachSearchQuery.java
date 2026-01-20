package de.coachkompass.backend.domain.coach;

import java.math.BigDecimal;

public record CoachSearchQuery(
        String sportSlug,
        String specializationSlug,
        Boolean remote,
        Boolean inPerson,
        String city,
        BigDecimal priceMax
) {}