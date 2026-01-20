package de.coachkompass.backend.infrastructure.coach;

import de.coachkompass.backend.domain.coach.Coach;
import de.coachkompass.backend.domain.coach.CoachRepository;
import de.coachkompass.backend.domain.coach.CoachSearchQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CoachRepositoryImpl implements CoachRepository {


    private final CoachCrudRepository crudRepo;

    public CoachRepositoryImpl(CoachCrudRepository crudRepo) {
        this.crudRepo = crudRepo;
    }

    @Override
    public List<Coach> findAllPublished() {
        return crudRepo.findByStatus("PUBLISHED").stream()
                .map(CoachMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Coach> findBySlug(String slug) {
        return crudRepo.findBySlug(slug)
                .map(CoachMapper::toDomain);
    }

    @Override
    public List<Coach> searchPublished(CoachSearchQuery query) {
        return crudRepo.searchPublished(
                        query.sportSlug(),
                        query.specializationSlug(),
                        query.remote(),
                        query.inPerson(),
                        query.city(),
                        query.priceMax()
                ).stream()
                .map(CoachMapper::toDomain)
                .toList();
    }
}
