package de.coachkompass.backend.application.myprofile;

import de.coachkompass.backend.domain.myprofile.CoachProfile;
import de.coachkompass.backend.domain.myprofile.MyCoachProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MyCoachProfileController {

    private final MyCoachProfileService service;

    public MyCoachProfileController(MyCoachProfileService service) {
        this.service = service;
    }

    @GetMapping("/api/my/coach-profile")
    public ResponseEntity<?> get(@AuthenticationPrincipal Jwt jwt) {
        return service.getMyProfile(jwt.getSubject())
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/api/my/coach-profile")
    public ResponseEntity<MyCoachProfileDto> upsert(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MyCoachProfileDto dto
    ) {
        return ResponseEntity.ok(toDto(service.upsertMyProfile(jwt.getSubject(), toDomain(dto))));
    }

    @PostMapping("/api/my/coach-profile/publish")
    public ResponseEntity<MyCoachProfileDto> publish(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(toDto(service.publishMyProfile(jwt.getSubject())));
    }

    @PostMapping("/api/my/coach-profile/unpublish")
    public ResponseEntity<MyCoachProfileDto> unpublish(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(toDto(service.unpublishMyProfile(jwt.getSubject())));
    }

    private CoachProfile toDomain(MyCoachProfileDto dto) {
        var socialLinks = dto.socialLinks() == null ? List.<CoachProfile.SocialLink>of() :
                dto.socialLinks().stream()
                        .map(s -> new CoachProfile.SocialLink(s.platform(), s.url()))
                        .toList();
        return new CoachProfile(
                null, dto.displayName(), dto.slug(),
                dto.bio(), dto.websiteUrl(),
                dto.city(), dto.region(), dto.country(),
                dto.remoteAvailable(), dto.inPersonAvailable(),
                dto.priceMin(), dto.priceMax(), dto.pricingModel(),
                dto.currency(), dto.status(),
                dto.sportSlugs(), dto.specializationSlugs(),
                socialLinks
        );
    }

    private MyCoachProfileDto toDto(CoachProfile p) {
        var socialLinks = p.socialLinks() == null ? List.<de.coachkompass.backend.application.coach.SocialLinkDto>of() :
                p.socialLinks().stream()
                        .map(s -> new de.coachkompass.backend.application.coach.SocialLinkDto(s.platform(), s.url()))
                        .toList();
        return new MyCoachProfileDto(
                p.displayName(), p.bio(), p.websiteUrl(),
                p.city(), p.region(), p.country(),
                p.remoteAvailable(), p.inPersonAvailable(),
                p.priceMin(), p.priceMax(), p.pricingModel(),
                p.currency(),
                p.sportSlugs(), p.specializationSlugs(),
                socialLinks,
                p.status(), p.slug()
        );
    }
}