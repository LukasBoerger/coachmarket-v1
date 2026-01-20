package de.coachkompass.backend.domain.coach;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoachService {

    private final CoachRepository coachRepo;

    public CoachService(CoachRepository coachRepo) {
        this.coachRepo = coachRepo;
    }

    public List<Coach> listPublished() {
        return coachRepo.findAllPublished();
    }

    public Optional<Coach> getBySlug(String slug) {
        return coachRepo.findBySlug(slug);
    }

    public List<Coach> searchPublished(CoachSearchQuery query) {
        return coachRepo.searchPublished(query);
    }
}
