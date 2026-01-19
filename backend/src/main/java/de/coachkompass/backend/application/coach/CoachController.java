package de.coachkompass.backend.application.coach;


import de.coachkompass.backend.domain.coach.CoachService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CoachController {

    private final CoachService service;

    public CoachController(CoachService service) {
        this.service = service;
    }

    @GetMapping("/api/coaches")
    public List<CoachDto> list() {
        return service.listPublished().stream().map(CoachDtoMapper::toDto).toList();
    }

    @GetMapping("/api/coaches/{slug}")
    public ResponseEntity<CoachDto> bySlug(@PathVariable String slug) {
        return service.getBySlug(slug)
                .map(CoachDtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}