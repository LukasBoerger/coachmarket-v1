package de.coachkompass.backend.infrastructure.coachsport;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CoachSportId implements Serializable {

    @Column(name = "coach_id", columnDefinition = "uuid")
    private UUID coachId;

    @Column(name = "sport_id", columnDefinition = "uuid")
    private UUID sportId;
}
