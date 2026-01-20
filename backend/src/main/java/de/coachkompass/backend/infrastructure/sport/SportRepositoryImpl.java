package de.coachkompass.backend.infrastructure.sport;

import de.coachkompass.backend.application.sport.SportDto;
import de.coachkompass.backend.application.sport.SportDtoMapper;
import de.coachkompass.backend.domain.sport.Sport;
import de.coachkompass.backend.domain.sport.SportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SportRepositoryImpl implements SportRepository {

    private final SportCrudRepository sportCrudRepo;

    public SportRepositoryImpl(SportCrudRepository sportCrudRepo) {
        this.sportCrudRepo = sportCrudRepo;
    }

    @Override
    public List<Sport> listPublished() {
        return this.sportCrudRepo.findAll().stream().map(SportMapper::toDomain).toList();
    }

    @Override
    public Optional<Sport> getBySlug(String slug) {
        return sportCrudRepo.findBySlug(slug).map(SportMapper::toDomain);
    }
}
