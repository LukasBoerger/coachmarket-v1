package de.coachkompass.backend.application.myprofile;

import de.coachkompass.backend.domain.myprofile.MyCoachProfileService;
import de.coachkompass.backend.infrastructure.media.MediaAssetEntity;
import de.coachkompass.backend.infrastructure.media.MediaAssetJpaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class MyCoachProfileController {

    private final MyCoachProfileService service;
    private final MediaAssetJpaRepository mediaRepo;

    public MyCoachProfileController(MyCoachProfileService service, MediaAssetJpaRepository mediaRepo) {
        this.service = service;
        this.mediaRepo = mediaRepo;
    }

    @GetMapping("/api/my/coach-profile")
    public ResponseEntity<?> get(@AuthenticationPrincipal Jwt jwt) {
        return service.getMyProfile(jwt.getSubject())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/api/my/coach-profile")
    public ResponseEntity<MyCoachProfileDto> upsert(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MyCoachProfileDto dto
    ) {
        return ResponseEntity.ok(service.upsertMyProfile(jwt.getSubject(), dto));
    }

    @PostMapping("/api/my/coach-profile/publish")
    public ResponseEntity<MyCoachProfileDto> publish(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.publishMyProfile(jwt.getSubject()));
    }

    @PostMapping("/api/my/coach-profile/unpublish")
    public ResponseEntity<MyCoachProfileDto> unpublish(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.unpublishMyProfile(jwt.getSubject()));
    }

    @PostMapping("/api/my/coach-profile/avatar")
    public ResponseEntity<Void> setAvatar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AvatarRequest request
    ) {
        service.getMyProfile(jwt.getSubject()).ifPresent(profile -> {
            // Alte Avatare löschen
            var coachId = service.getCoachId(jwt.getSubject());
            mediaRepo.findByCoachIdAndTypeAndVisibilityOrderByCreatedAtAsc(coachId, "IMAGE", "PUBLIC")
                    .forEach(m -> mediaRepo.delete(m));

            // Neues Bild speichern
            mediaRepo.save(MediaAssetEntity.builder()
                    .id(UUID.randomUUID())
                    .coachId(coachId)
                    .type("IMAGE")
                    .url(request.url())
                    .visibility("PUBLIC")
                    .createdAt(OffsetDateTime.now())
                    .build());
        });
        return ResponseEntity.ok().build();
    }

    public record AvatarRequest(String url) {}
}