package de.coachkompass.backend.domain.specialization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@AllArgsConstructor
@Builder
@Data
public class Specialization {

    private final UUID id;
    private final String name;
    private final String slug;

}