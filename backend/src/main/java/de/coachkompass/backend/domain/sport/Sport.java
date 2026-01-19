package de.coachkompass.backend.domain.sport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class Sport {
    private final UUID id;
    private final String name;
    private final String slug;
}
