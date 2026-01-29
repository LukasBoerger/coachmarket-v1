package de.coachkompass.backend.application.coach;


import de.coachkompass.backend.domain.coach.CoachSearchQuery;
import de.coachkompass.backend.domain.coach.CoachService;
import de.coachkompass.backend.infrastructure.media.MediaAssetJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class CoachController {

    private final CoachService service;
    private final MediaAssetJpaRepository mediaRepo;


    public CoachController(CoachService service, MediaAssetJpaRepository mediaRepo) {
        this.service = service;
        this.mediaRepo = mediaRepo;
    }

//    @GetMapping("/api/coaches")
//    public List<CoachDto> list() {
//        return service.listPublished().stream().map(CoachDtoMapper::toDto).toList();
//    }

    @GetMapping("/api/coaches/{slug}")
    public ResponseEntity<CoachDto> bySlug(@PathVariable String slug) {
        return service.getBySlug(slug)
                .map(coach -> {
                    var images = mediaRepo.findByCoachIdAndTypeAndVisibilityOrderByCreatedAtAsc(
                            coach.getId(), "IMAGE", "PUBLIC"
                    ).stream().map(m -> m.getUrl()).toList();

                    String avatar = images.isEmpty() ? null : images.get(0);
                    return CoachDtoMapper.toDto(coach, avatar, images);
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
        CoachSearchQuery query = new CoachSearchQuery(sportSlug, specializationSlug, remote, inPerson, city, priceMax);

        return service.searchPublished(query).stream()
                .map(coach -> {
                    var images = mediaRepo.findByCoachIdAndTypeAndVisibilityOrderByCreatedAtAsc(
                            coach.getId(), "IMAGE", "PUBLIC"
                    ).stream().map(m -> m.getUrl()).toList();

                    String avatar = images.isEmpty() ? null : images.get(0);
                    // für List-View kannst du imageUrls auch leer lassen, wenn du willst:
                    return CoachDtoMapper.toDto(coach, avatar, images);
                })
                .toList();
    }

    @GetMapping("/api/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "uid", jwt.getSubject(),
                "email", jwt.getClaimAsString("email")
        );
    }

}