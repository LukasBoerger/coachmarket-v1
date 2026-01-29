package de.coachkompass.backend.infrastructure.coachspezialisation;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coach_specialization")
@Getter
@Setter
public class CoachSpecializationEntity {

    @EmbeddedId
    private CoachSpecializationId id;

    @Column(nullable = false)
    private int priority;
}
