package de.coachkompass.backend.infrastructure.coach;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "coach")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CoachEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "account_id", columnDefinition = "uuid", nullable = true, unique = true)
    private UUID accountId;

    @Column(name="display_name", nullable=false)
    private String displayName;

    @Column(nullable=false, unique=true)
    private String slug;

    @Column(columnDefinition="text")
    private String bio;

    @Column(name="website_url")
    private String websiteUrl;

    private String city;

    @Column(name="remote_available", nullable=false)
    private boolean remoteAvailable;

    @Column(name="in_person_available", nullable=false)
    private boolean inPersonAvailable;

    @Column(name="price_min", precision=10, scale=2)
    private BigDecimal priceMin;

    @Column(name="price_max", precision=10, scale=2)
    private BigDecimal priceMax;

    @Column(nullable=false)
    private String currency;

    @Column(nullable=false)
    private String status;

    @Column(name="created_at", nullable=false)
    private OffsetDateTime createdAt;

    @Column(name="updated_at", nullable=false)
    private OffsetDateTime updatedAt;

}
