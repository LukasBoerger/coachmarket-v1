package de.coachkompass.backend.infrastructure.coachsport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "coach_sport")
@Getter
@Setter
public class CoachSportEntity {

    @Id
    private UUID id;

    @Column(name = "coach_id", nullable = false)
    private UUID coachId;

    @Column(name = "sport_id", nullable = false)
    private UUID sportId;

    private int priority;
}
