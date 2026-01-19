package de.coachkompass.backend.domain.sport;

import de.coachkompass.backend.application.sport.SportDto;

import java.util.List;
import java.util.Optional;

public interface SportRepository{
    public List<Sport> listPublished();

    public Optional<Sport> getBySlug(String slug);
}