package de.coachkompass.backend.application.sport;

import de.coachkompass.backend.domain.coach.Coach;

import java.util.UUID;

public record SportDto(UUID id, String name, String slug) {
}
