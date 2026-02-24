package de.coachkompass.backend.domain.coach;

import de.coachkompass.backend.application.coach.SocialLinkDto;
import de.coachkompass.backend.infrastructure.leadclick.LeadClickCrudRepository;
import de.coachkompass.backend.infrastructure.leadclick.LeadClickEntity;
import de.coachkompass.backend.infrastructure.media.MediaAssetJpaRepository;
import de.coachkompass.backend.infrastructure.socialmedia.SocialMediaLinkCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoachService {

    private final CoachRepository coachRepo;
    private final MediaAssetJpaRepository mediaRepo;
    private final SocialMediaLinkCrudRepository socialRepo;
    private final LeadClickCrudRepository leadClickRepo;

    public CoachService(
            CoachRepository coachRepo,
            MediaAssetJpaRepository mediaRepo,
            SocialMediaLinkCrudRepository socialRepo,
            LeadClickCrudRepository leadClickRepo
    ) {
        this.coachRepo = coachRepo;
        this.mediaRepo = mediaRepo;
        this.socialRepo = socialRepo;
        this.leadClickRepo = leadClickRepo;
    }

    public List<CoachWithMedia> searchPublishedWithMedia(CoachSearchQuery query) {
        var coaches = coachRepo.searchPublished(query);
        if (coaches.isEmpty()) return List.of();

        var coachIds = coaches.stream().map(Coach::getId).toList();

        // Alle Images in einem Query
        var imagesByCoach = mediaRepo.findByCoachIdInAndTypeAndVisibility(coachIds, "IMAGE", "PUBLIC")
                .stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCoachId(),
                        Collectors.mapping(m -> m.getUrl(), Collectors.toList())
                ));

        // Alle Social Links in einem Query
        var socialsByCoach = socialRepo.findAllByCoachIdIn(coachIds)
                .stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCoachId(),
                        Collectors.mapping(s -> new SocialLinkDto(s.getPlatform(), s.getUrl()), Collectors.toList())
                ));

        return coaches.stream().map(coach -> {
            var images = imagesByCoach.getOrDefault(coach.getId(), List.of());
            var socials = socialsByCoach.getOrDefault(coach.getId(), List.of());
            return new CoachWithMedia(coach, images, socials);
        }).toList();
    }

    public Optional<CoachWithMedia> getBySlugWithMedia(String slug) {
        return coachRepo.findBySlug(slug).map(coach -> {
            var images = getImageUrls(coach.getId());
            var socials = getSocialLinks(coach.getId());
            return new CoachWithMedia(coach, images, socials);
        });
    }

    public List<String> getImageUrls(UUID coachId) {
        return mediaRepo.findByCoachIdAndTypeAndVisibilityOrderByCreatedAtAsc(coachId, "IMAGE", "PUBLIC")
                .stream().map(m -> m.getUrl()).toList();
    }

    public List<SocialLinkDto> getSocialLinks(UUID coachId) {
        return socialRepo.findAllByCoachIdOrderByDisplayOrderAsc(coachId)
                .stream()
                .map(s -> new SocialLinkDto(s.getPlatform(), s.getUrl()))
                .toList();
    }

    public void trackLeadClick(UUID coachId, String type) {
        leadClickRepo.save(LeadClickEntity.builder()
                .id(UUID.randomUUID())
                .coachId(coachId)
                .type(type)
                .createdAt(OffsetDateTime.now())
                .build());
    }

    public record CoachWithMedia(Coach coach, List<String> imageUrls, List<SocialLinkDto> socialLinks) {}
}