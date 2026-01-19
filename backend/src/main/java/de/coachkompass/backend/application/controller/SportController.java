package de.coachkompass.backend.application.controller;

import java.util.List;

import de.coachkompass.backend.domain.sport.SportRepository;
import de.coachkompass.backend.infrastructure.entities.SportEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SportController {

    private final SportRepository repo;

    public SportController(SportRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/sports")
    public List<SportEntity> list() {
        return repo.findAll();
    }
}
