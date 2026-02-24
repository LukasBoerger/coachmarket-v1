package de.coachkompass.backend.domain.sport;

import java.util.List;
import java.util.Optional;

public interface SportRepository {
    List<Sport> listPublished();
    Optional<Sport> getBySlug(String slug);
}