package de.coachkompass.backend.application.myprofile;

import de.coachkompass.backend.domain.myprofile.MyCoachProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class MyCoachProfileController {

    private final MyCoachProfileService service;

    public MyCoachProfileController(MyCoachProfileService service) {
        this.service = service;
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
}