package de.coachkompass.backend.application.coach;

import de.coachkompass.backend.domain.coach.CoachSearchQuery;
import de.coachkompass.backend.domain.coach.CoachService;
import de.coachkompass.backend.infrastructure.leadclick.LeadClickCrudRepository;
import de.coachkompass.backend.infrastructure.leadclick.LeadClickEntity;
import de.coachkompass.backend.infrastructure.media.MediaAssetJpaRepository;
import de.coachkompass.backend.infrastructure.socialmedia.SocialMediaLinkCrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class CoachController {

    private final CoachService service;
    private final MediaAssetJpaRepository mediaRepo;
    private final SocialMediaLinkCrudRepository socialRepo;
    private final LeadClickCrudRepository leadClickRepo;

    public CoachController(
            CoachService service,
            MediaAssetJpaRepository mediaRepo,
            SocialMediaLinkCrudRepository socialRepo,
            LeadClickCrudRepository leadClickRepo
    ) {
        this.service = service;
        this.mediaRepo = mediaRepo;
        this.socialRepo = socialRepo;
        this.leadClickRepo = leadClickRepo;
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
        return service.searchPublished(query).stream()
                .map(coach -> {
                    var images = imageUrls(coach.getId());
                    var socials = socialLinks(coach.getId());
                    return CoachDtoMapper.toDto(coach, images.isEmpty() ? null : images.get(0), images, socials);
                })
                .toList();
    }

    @GetMapping("/api/coaches/{slug}")
    public ResponseEntity<CoachDto> bySlug(@PathVariable String slug) {
        return service.getBySlug(slug)
                .map(coach -> {
                    var images = imageUrls(coach.getId());
                    var socials = socialLinks(coach.getId());
                    return CoachDtoMapper.toDto(coach, images.isEmpty() ? null : images.get(0), images, socials);
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/coaches/{slug}/lead-click")
    public ResponseEntity<Void> trackLeadClick(
            @PathVariable String slug,
            @RequestParam String type
    ) {
        if (!type.equals("WEBSITE_CLICK") && !type.equals("CONTACT_CLICK")) {
            return ResponseEntity.badRequest().build();
        }
        service.getBySlug(slug).ifPresent(coach -> {
            leadClickRepo.save(LeadClickEntity.builder()
                    .id(UUID.randomUUID())
                    .coachId(coach.getId())
                    .type(type)
                    .createdAt(OffsetDateTime.now())
                    .build());
        });
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "uid", jwt.getSubject(),
                "email", jwt.getClaimAsString("email")
        );
    }

    private List<String> imageUrls(UUID coachId) {
        return mediaRepo.findByCoachIdAndTypeAndVisibilityOrderByCreatedAtAsc(coachId, "IMAGE", "PUBLIC")
                .stream().map(m -> m.getUrl()).toList();
    }

    private List<SocialLinkDto> socialLinks(UUID coachId) {
        return socialRepo.findAllByCoachIdOrderByDisplayOrderAsc(coachId)
                .stream()
                .map(s -> new SocialLinkDto(s.getPlatform(), s.getUrl()))
                .toList();
    }
}