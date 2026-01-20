package de.coachkompass.backend.application.specialization;

import org.springframework.web.bind.annotation.RestController;

import de.coachkompass.backend.domain.specialization.SpecializationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SpecializationController {

    private final SpecializationService service;

    public SpecializationController(SpecializationService service) {
        this.service = service;
    }

    @GetMapping("/api/specializations")
    public List<SpecializationDto> list() {
        return service.listAll().stream()
                .map(SpecializationDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/api/specializations/{slug}")
    public ResponseEntity<SpecializationDto> bySlug(@PathVariable String slug) {
        return service.getBySlug(slug)
                .map(SpecializationDtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
