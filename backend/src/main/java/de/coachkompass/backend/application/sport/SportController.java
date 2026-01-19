package de.coachkompass.backend.application.sport;

import de.coachkompass.backend.domain.sport.SportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SportController {

    private final SportService service;

    public SportController(SportService service) {
        this.service = service;
    }

    @GetMapping("/api/sports")
    public List<SportDto> list() {
        return service.listAll().stream()
                .map(SportDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/api/sports/{slug}")
    public ResponseEntity<SportDto> bySlug(@PathVariable String slug) {
        return service.getBySlug(slug)
                .map(SportDtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}