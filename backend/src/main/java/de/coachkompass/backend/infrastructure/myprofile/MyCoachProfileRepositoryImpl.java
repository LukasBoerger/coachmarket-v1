package de.coachkompass.backend.infrastructure.myprofile;

import de.coachkompass.backend.domain.myprofile.CoachProfile;
import de.coachkompass.backend.domain.myprofile.MyCoachProfileRepository;
import de.coachkompass.backend.domain.util.SlugUtil;
import de.coachkompass.backend.infrastructure.coach.*;
import de.coachkompass.backend.infrastructure.coachspezialisation.*;
import de.coachkompass.backend.infrastructure.coachsport.*;
import de.coachkompass.backend.infrastructure.socialmedia.*;
import de.coachkompass.backend.infrastructure.specialization.SpecializationCrudRepository;
import de.coachkompass.backend.infrastructure.sport.SportCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MyCoachProfileRepositoryImpl implements MyCoachProfileRepository {

    private final CoachCrudRepository coachRepo;
    private final CoachSportCrudRepository coachSportRepo;
    private final CoachSpecializationCrudRepository coachSpecRepo;
    private final SportCrudRepository sportRepo;
    private final SpecializationCrudRepository specRepo;
    private final SocialMediaLinkCrudRepository socialRepo;

    public MyCoachProfileRepositoryImpl(
            CoachCrudRepository coachRepo,
            CoachSportCrudRepository coachSportRepo,
            CoachSpecializationCrudRepository coachSpecRepo,
            SportCrudRepository sportRepo,
            SpecializationCrudRepository specRepo,
            SocialMediaLinkCrudRepository socialRepo
    ) {
        this.coachRepo = coachRepo;
        this.coachSportRepo = coachSportRepo;
        this.coachSpecRepo = coachSpecRepo;
        this.sportRepo = sportRepo;
        this.specRepo = specRepo;
        this.socialRepo = socialRepo;
    }

    @Override
    public Optional<CoachProfile> findByAccountId(UUID accountId) {
        return coachRepo.findByAccountId(accountId).map(this::loadProfile);
    }

    @Override
    @Transactional
    public CoachProfile upsert(UUID accountId, CoachProfile profile) {
        var now = OffsetDateTime.now();
        CoachEntity coach = coachRepo.findByAccountId(accountId)
                .orElseGet(() -> createCoachSkeleton(accountId, profile.displayName(), profile.currency(), now));

        coach.setDisplayName(profile.displayName());
        coach.setBio(profile.bio());
        coach.setWebsiteUrl(profile.websiteUrl());
        coach.setCity(profile.city());
        coach.setRegion(profile.region());
        coach.setCountry(profile.country());
        coach.setRemoteAvailable(profile.remoteAvailable());
        coach.setInPersonAvailable(profile.inPersonAvailable());
        coach.setPriceMin(profile.priceMin());
        coach.setPriceMax(profile.priceMax());
        coach.setPricingModel(profile.pricingModel());
        coach.setCurrency(profile.currency() == null ? "EUR" : profile.currency());
        coach.setUpdatedAt(now);
        coachRepo.save(coach);

        replaceSports(coach, profile.sportSlugs());
        replaceSpecializations(coach, profile.specializationSlugs());
        replaceSocialLinks(coach, profile.socialLinks());

        return loadProfile(coach);
    }

    @Override
    public void setStatus(UUID coachId, String status) {
        var coach = coachRepo.findById(coachId).orElseThrow();
        coach.setStatus(status);
        coach.setUpdatedAt(OffsetDateTime.now());
        coachRepo.save(coach);
    }

    // ---- internals ----

    private CoachEntity createCoachSkeleton(UUID accountId, String displayName, String currency, OffsetDateTime now) {
        String slug = ensureUniqueSlug(SlugUtil.slugify(displayName));
        CoachEntity c = CoachEntity.builder()
                .id(UUID.randomUUID())
                .accountId(accountId)
                .displayName(displayName)
                .slug(slug)
                .remoteAvailable(true)
                .inPersonAvailable(true)
                .currency(currency == null ? "EUR" : currency)
                .status("DRAFT")
                .createdAt(now)
                .updatedAt(now)
                .build();
        return coachRepo.save(c);
    }

    private String ensureUniqueSlug(String base) {
        String candidate = base;
        int i = 2;
        while (coachRepo.existsBySlug(candidate)) {
            candidate = base + "-" + i++;
        }
        return candidate;
    }

    private void replaceSports(CoachEntity coach, List<String> slugs) {
        coachSportRepo.deleteAllById_CoachId(coach.getId());
        int prio = 0;
        for (String slug : safeList(slugs)) {
            int p = prio++;
            sportRepo.findBySlug(slug).ifPresent(sport -> {
                CoachSportEntity cs = new CoachSportEntity();
                cs.setId(new CoachSportId(coach.getId(), sport.getId()));
                cs.setPriority(p);
                coachSportRepo.save(cs);
            });
        }
    }

    private void replaceSpecializations(CoachEntity coach, List<String> slugs) {
        coachSpecRepo.deleteAllById_CoachId(coach.getId());
        int prio = 0;
        for (String slug : safeList(slugs)) {
            int p = prio++;
            specRepo.findBySlug(slug).ifPresent(spec -> {
                CoachSpecializationEntity csp = new CoachSpecializationEntity();
                csp.setId(new CoachSpecializationId(coach.getId(), spec.getId()));
                csp.setPriority(p);
                coachSpecRepo.save(csp);
            });
        }
    }

    private void replaceSocialLinks(CoachEntity coach, List<CoachProfile.SocialLink> links) {
        socialRepo.deleteAllByCoachId(coach.getId());
        if (links == null) return;
        int order = 0;
        for (CoachProfile.SocialLink link : links) {
            socialRepo.save(SocialMediaLinkEntity.builder()
                    .id(UUID.randomUUID())
                    .coachId(coach.getId())
                    .platform(link.platform())
                    .url(link.url())
                    .displayOrder(order++)
                    .createdAt(OffsetDateTime.now())
                    .build());
        }
    }

    private CoachProfile loadProfile(CoachEntity coach) {
        var sports = coachSportRepo.findAllById_CoachIdOrderByPriorityAsc(coach.getId()).stream()
                .map(cs -> sportRepo.findById(cs.getId().getSportId()).map(s -> s.getSlug()).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toList());

        var specs = coachSpecRepo.findAllById_CoachIdOrderByPriorityAsc(coach.getId()).stream()
                .map(cs -> specRepo.findById(cs.getId().getSpecializationId()).map(s -> s.getSlug()).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toList());

        var socials = socialRepo.findAllByCoachIdOrderByDisplayOrderAsc(coach.getId()).stream()
                .map(s -> new CoachProfile.SocialLink(s.getPlatform(), s.getUrl()))
                .collect(Collectors.toList());

        return new CoachProfile(
                coach.getId(), coach.getDisplayName(), coach.getSlug(),
                coach.getBio(), coach.getWebsiteUrl(),
                coach.getCity(), coach.getRegion(), coach.getCountry(),
                coach.isRemoteAvailable(), coach.isInPersonAvailable(),
                coach.getPriceMin(), coach.getPriceMax(), coach.getPricingModel(),
                coach.getCurrency(), coach.getStatus(),
                sports, specs, socials
        );
    }

    private static <T> List<T> safeList(List<T> in) {
        return in == null ? List.of() : in;
    }
}