package de.coachkompass.backend.infrastructure.coach;

import de.coachkompass.backend.domain.coach.Coach;
import de.coachkompass.backend.domain.coach.CoachRepository;
import de.coachkompass.backend.domain.coach.CoachSearchQuery;
import de.coachkompass.backend.infrastructure.coachspezialisation.CoachSpecializationCrudRepository;
import de.coachkompass.backend.infrastructure.coachsport.CoachSportCrudRepository;
import de.coachkompass.backend.infrastructure.specialization.SpecializationCrudRepository;
import de.coachkompass.backend.infrastructure.specialization.SpecializationEntity;
import de.coachkompass.backend.infrastructure.sport.SportCrudRepository;
import de.coachkompass.backend.infrastructure.sport.SportEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CoachRepositoryImpl implements CoachRepository {

    private final CoachCrudRepository crudRepo;
    private final CoachSportCrudRepository coachSportRepo;
    private final CoachSpecializationCrudRepository coachSpecRepo;
    private final SportCrudRepository sportRepo;
    private final SpecializationCrudRepository specRepo;

    public CoachRepositoryImpl(
            CoachCrudRepository crudRepo,
            CoachSportCrudRepository coachSportRepo,
            CoachSpecializationCrudRepository coachSpecRepo,
            SportCrudRepository sportRepo,
            SpecializationCrudRepository specRepo
    ) {
        this.crudRepo = crudRepo;
        this.coachSportRepo = coachSportRepo;
        this.coachSpecRepo = coachSpecRepo;
        this.sportRepo = sportRepo;
        this.specRepo = specRepo;
    }

    @Override
    public List<Coach> findAllPublished() {
        return crudRepo.findByStatus("PUBLISHED").stream()
                .map(e -> CoachMapper.toDomain(e, loadSportSlugs(e.getId()), loadSpecSlugs(e.getId())))
                .toList();
    }

    @Override
    public Optional<Coach> findBySlug(String slug) {
        return crudRepo.findBySlug(slug)
                .map(e -> CoachMapper.toDomain(e, loadSportSlugs(e.getId()), loadSpecSlugs(e.getId())));
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
                .map(r -> CoachMapper.toDomain(r, loadSportSlugs(r.getId()), loadSpecSlugs(r.getId())))
                .toList();
    }

    @Override
    public List<String> findSportSlugsByCoachId(UUID coachId) {
        return loadSportSlugs(coachId);
    }

    @Override
    public List<String> findSpecializationSlugsByCoachId(UUID coachId) {
        return loadSpecSlugs(coachId);
    }

    // ---- internals ----

    private List<String> loadSportSlugs(UUID coachId) {
        return coachSportRepo.findAllById_CoachIdOrderByPriorityAsc(coachId).stream()
                .map(cs -> sportRepo.findById(cs.getId().getSportId())
                        .map(SportEntity::getSlug).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<String> loadSpecSlugs(UUID coachId) {
        return coachSpecRepo.findAllById_CoachIdOrderByPriorityAsc(coachId).stream()
                .map(cs -> specRepo.findById(cs.getId().getSpecializationId())
                        .map(SpecializationEntity::getSlug).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}