package de.coachkompass.backend.infrastructure.leadclick;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "lead_click")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LeadClickEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "coach_id", nullable = false)
    private UUID coachId;

    @Column(nullable = false)
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> meta;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}