package de.coachkompass.backend.infrastructure.specialization;

import de.coachkompass.backend.domain.specialization.Specialization;
import de.coachkompass.backend.domain.specialization.SpecializationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SpecializationRepositoryImpl implements SpecializationRepository {

    private final SpecializationCrudRepository crudRepo;

    public SpecializationRepositoryImpl(SpecializationCrudRepository crudRepo) {
        this.crudRepo = crudRepo;
    }

    @Override
    public List<Specialization> findAll() {
        return crudRepo.findAll().stream()
                .map(SpecializationMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Specialization> getBySlug(String slug) {
        return crudRepo.findBySlug(slug)
                .map(SpecializationMapper::toDomain);
    }
}