package de.coachkompass.backend.domain.coach;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
}
