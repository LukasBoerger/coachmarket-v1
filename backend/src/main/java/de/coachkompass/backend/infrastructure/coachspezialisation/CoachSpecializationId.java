package de.coachkompass.backend.infrastructure.coachspezialisation;

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
public class CoachSpecializationId implements Serializable {

    @Column(name = "coach_id", columnDefinition = "uuid")
    private UUID coachId;

    @Column(name = "specialization_id", columnDefinition = "uuid")
    private UUID specializationId;
}
