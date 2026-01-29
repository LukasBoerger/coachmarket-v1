package de.coachkompass.backend.infrastructure.media;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_asset")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MediaAssetEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "coach_id", nullable = false, columnDefinition = "uuid")
    private UUID coachId;

    @Column(nullable = false)
    private String type; // IMAGE / VIDEO

    @Column(nullable = false, length = 700)
    private String url;

    @Column(nullable = false)
    private String visibility; // PUBLIC / PREMIUM_ONLY

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
