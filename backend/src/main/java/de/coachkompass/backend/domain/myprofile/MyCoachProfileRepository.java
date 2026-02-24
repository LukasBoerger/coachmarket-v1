package de.coachkompass.backend.domain.myprofile;

import de.coachkompass.backend.application.coach.SocialLinkDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MyCoachProfileRepository {

    Optional<CoachProfile> findByAccountId(UUID accountId);
    void setStatus(UUID coachId, String status);
    CoachProfile upsert(UUID accountId, CoachProfile profile);


}