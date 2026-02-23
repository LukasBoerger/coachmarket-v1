package de.coachkompass.backend.infrastructure.myprofile;

import de.coachkompass.backend.application.coach.SocialLinkDto;
import de.coachkompass.backend.domain.myprofile.MyCoachProfileRepository;
import de.coachkompass.backend.domain.util.SlugUtil;
import de.coachkompass.backend.infrastructure.coach.*;
import de.coachkompass.backend.infrastructure.coachspezialisation.CoachSpecializationCrudRepository;
import de.coachkompass.backend.infrastructure.coachspezialisation.CoachSpecializationEntity;
import de.coachkompass.backend.infrastructure.coachspezialisation.CoachSpecializationId;
import de.coachkompass.backend.infrastructure.coachsport.CoachSportCrudRepository;
import de.coachkompass.backend.infrastructure.coachsport.CoachSportEntity;
import de.coachkompass.backend.infrastructure.coachsport.CoachSportId;
import de.coachkompass.backend.infrastructure.socialmedia.SocialMediaLinkCrudRepository;
import de.coachkompass.backend.infrastructure.socialmedia.SocialMediaLinkEntity;
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
    public Optional<CoachProfileAggregate> findByAccountId(UUID accountId) {
        return coachRepo.findByAccountId(accountId).map(this::loadAggregate);
    }

    @Override
    @Transactional
    public CoachProfileAggregate upsert(UUID accountId, CoachProfileAggregate agg) {
        var now = OffsetDateTime.now();
        CoachEntity coach = coachRepo.findByAccountId(accountId)
                .orElseGet(() -> createCoachSkeleton(accountId, agg.displayName(), agg.currency(), now));

        coach.setDisplayName(agg.displayName());
        coach.setBio(agg.bio());
        coach.setWebsiteUrl(agg.websiteUrl());
        coach.setCity(agg.city());
        coach.setRegion(agg.region());
        coach.setCountry(agg.country());
        coach.setRemoteAvailable(agg.remoteAvailable());
        coach.setInPersonAvailable(agg.inPersonAvailable());
        coach.setPriceMin(agg.priceMin());
        coach.setPriceMax(agg.priceMax());
        coach.setPricingModel(agg.pricingModel());
        coach.setCurrency(agg.currency() == null ? "EUR" : agg.currency());
        coach.setUpdatedAt(now);
        coachRepo.save(coach);

        replaceSports(coach, agg.sportSlugs());
        replaceSpecializations(coach, agg.specializationSlugs());
        replaceSocialLinks(coach, agg.socialLinks());

        return loadAggregate(coach);
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

    private void replaceSocialLinks(CoachEntity coach, List<SocialLinkDto> links) {
        socialRepo.deleteAllByCoachId(coach.getId());
        if (links == null) return;
        int order = 0;
        for (SocialLinkDto link : links) {
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

    private CoachProfileAggregate loadAggregate(CoachEntity coach) {
        var sports = coachSportRepo.findAllById_CoachIdOrderByPriorityAsc(coach.getId()).stream()
                .map(cs -> sportRepo.findById(cs.getId().getSportId()).map(s -> s.getSlug()).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toList());

        var specs = coachSpecRepo.findAllById_CoachIdOrderByPriorityAsc(coach.getId()).stream()
                .map(cs -> specRepo.findById(cs.getId().getSpecializationId()).map(s -> s.getSlug()).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toList());

        var socials = socialRepo.findAllByCoachIdOrderByDisplayOrderAsc(coach.getId()).stream()
                .map(s -> new SocialLinkDto(s.getPlatform(), s.getUrl()))
                .collect(Collectors.toList());

        return new CoachProfileAggregate(
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