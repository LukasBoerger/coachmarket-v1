package de.coachkompass.backend.infrastructure.socialmedia;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "social_media_link")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SocialMediaLinkEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "coach_id", nullable = false)
    private UUID coachId;

    @Column(nullable = false)
    private String platform;

    @Column(nullable = false)
    private String url;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}