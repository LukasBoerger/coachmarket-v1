package de.coachkompass.backend.domain.specialization;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecializationService {

    private final SpecializationRepository repo;

    public SpecializationService(SpecializationRepository repo) {
        this.repo = repo;
    }

    public List<Specialization> listAll() {
        return repo.findAll();
    }

    public Optional<Specialization> getBySlug(String slug) {
        return repo.getBySlug(slug);
    }
}