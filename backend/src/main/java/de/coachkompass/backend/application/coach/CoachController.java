package de.coachkompass.backend.application.coach;

import de.coachkompass.backend.domain.coach.CoachSearchQuery;
import de.coachkompass.backend.domain.coach.CoachService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class CoachController {

    private final CoachService service;

    public CoachController(CoachService service) {
        this.service = service;
    }

    @GetMapping("/api/coaches")
    public List<CoachDto> list(
            @RequestParam(required = false) String sportSlug,
            @RequestParam(required = false) String specializationSlug,
            @RequestParam(required = false) Boolean remote,
            @RequestParam(required = false) Boolean inPerson,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal priceMax
    ) {
        var query = new CoachSearchQuery(sportSlug, specializationSlug, remote, inPerson, city, priceMax);
        return service.searchPublishedWithMedia(query).stream()
                .map(cm -> CoachDtoMapper.toDto(
                        cm.coach(),
                        cm.imageUrls().isEmpty() ? null : cm.imageUrls().get(0),
                        cm.imageUrls(),
                        cm.socialLinks()
                ))
                .toList();
    }

    @GetMapping("/api/coaches/{slug}")
    public ResponseEntity<CoachDto> bySlug(@PathVariable String slug) {
        return service.getBySlugWithMedia(slug)
                .map(cm -> CoachDtoMapper.toDto(
                        cm.coach(),
                        cm.imageUrls().isEmpty() ? null : cm.imageUrls().get(0),
                        cm.imageUrls(),
                        cm.socialLinks()
                ))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "uid", jwt.getSubject(),
                "email", jwt.getClaimAsString("email")
        );
    }


}