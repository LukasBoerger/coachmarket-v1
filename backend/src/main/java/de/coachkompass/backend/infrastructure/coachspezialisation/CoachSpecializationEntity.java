package de.coachkompass.backend.infrastructure.coachspezialisation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "coach_specialization")
@Getter
@Setter
public class CoachSpecializationEntity {

    @Id
    private UUID id;

    @Column(name = "coach_id", nullable = false)
    private UUID coachId;

    @Column(name = "specialization_id", nullable = false)
    private UUID specializationId;

    private int priority;
}
