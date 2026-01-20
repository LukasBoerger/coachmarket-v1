package de.coachkompass.backend.application.specialization;

import java.util.UUID;

public record SpecializationDto(UUID id, String name, String slug) {}
