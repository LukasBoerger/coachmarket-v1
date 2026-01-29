package de.coachkompass.backend.infrastructure.coachsport;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coach_sport")
@Getter
@Setter
public class CoachSportEntity {

    @EmbeddedId
    private CoachSportId id;

    @Column(nullable = false)
    private int priority;
}
