package de.coachkompass.backend.infrastructure.sport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Entity
@Table(name = "sport")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SportEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
    private String slug;
}
