package de.coachkompass.backend.infrastructure.myprofile;

import de.coachkompass.backend.domain.myprofile.MyCoachProfileRepository;
import de.coachkompass.backend.domain.util.SlugUtil;
import de.coachkompass.backend.infrastructure.coach.*;
import de.coachkompass.backend.infrastructure.coachspezialisation.CoachSpecializationCrudRepository;
import de.coachkompass.backend.infrastructure.coachspezialisation.CoachSpecializationEntity;
import de.coachkompass.backend.infrastructure.coachspezialisation.CoachSpecializationId;
import de.coachkompass.backend.infrastructure.coachsport.CoachSportCrudRepository;
import de.coachkompass.backend.infrastructure.coachsport.CoachSportEntity;
import de.coachkompass.backend.infrastructure.coachsport.CoachSportId;
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

    public MyCoachProfileRepositoryImpl(
            CoachCrudRepository coachRepo,
            CoachSportCrudRepository coachSportRepo,
            CoachSpecializationCrudRepository coachSpecRepo,
            SportCrudRepository sportRepo,
            SpecializationCrudRepository specRepo
    ) {
        this.coachRepo = coachRepo;
        this.coachSportRepo = coachSportRepo;
        this.coachSpecRepo = coachSpecRepo;
        this.sportRepo = sportRepo;
        this.specRepo = specRepo;
    }

    @Override
    public Optional<CoachProfileAggregate> findByAccountId(UUID accountId) {
        return coachRepo.findByAccountId(accountId).map(this::loadAggregate);
    }

    @Override
    @Transactional
    public CoachProfileAggregate upsert(UUID accountId, CoachProfileAggregate aggregate) {
        CoachEntity coach = coachRepo.findByAccountId(accountId)
                .orElseGet(() -> createCoachSkeleton(accountId, aggregate.displayName(), aggregate.currency()));

        // update coach fields
        coach.setDisplayName(aggregate.displayName());
        coach.setBio(aggregate.bio());
        coach.setWebsiteUrl(aggregate.websiteUrl());
        coach.setCity(aggregate.city());
        coach.setRemoteAvailable(aggregate.remoteAvailable());
        coach.setInPersonAvailable(aggregate.inPersonAvailable());
        coach.setPriceMin(aggregate.priceMin());
        coach.setPriceMax(aggregate.priceMax());
        coach.setCurrency(aggregate.currency() == null ? "EUR" : aggregate.currency());
        // status: beim Erstellen DRAFT lassen; später kannst du hier Regeln reinpacken
        coachRepo.save(coach);

        replaceSports(coach, aggregate.sportSlugs());
        replaceSpecializations(coach, aggregate.specializationSlugs());

        return loadAggregate(coach);
    }

    // ---------- internals ----------

    private CoachEntity createCoachSkeleton(UUID accountId, String displayName, String currency) {
        String slugBase = SlugUtil.slugify(displayName);
        String slug = ensureUniqueSlug(slugBase);

        CoachEntity c = new CoachEntity();
        c.setId(UUID.randomUUID());
        c.setAccountId(accountId);        // falls du Relation nutzt: setAccount(accountEntity)
        c.setDisplayName(displayName);
        c.setSlug(slug);
        c.setRemoteAvailable(true);
        c.setInPersonAvailable(true);
        c.setCurrency(currency == null ? "EUR" : currency);
        c.setStatus("DRAFT");
        return coachRepo.save(c);
    }

    private String ensureUniqueSlug(String slugBase) {
        String candidate = slugBase;
        int i = 2;
        while (coachRepo.existsBySlug(candidate)) {
            candidate = slugBase + "-" + i++;
        }
        return candidate;
    }

    private void replaceSports(CoachEntity coach, List<String> sportSlugs) {
        coachSportRepo.deleteAllById_CoachId(coach.getId());

        int prio = 0;
        for (String slug : safeList(sportSlugs)) {
            int finalPrio = prio;
            sportRepo.findBySlug(slug).ifPresent(sport -> {
                CoachSportEntity cs = new CoachSportEntity();
                cs.setId(new CoachSportId(coach.getId(), sport.getId()));
                cs.setPriority(finalPrio);
                coachSportRepo.save(cs);
            });
            prio++;
        }
    }

    private void replaceSpecializations(CoachEntity coach, List<String> specSlugs) {
        coachSpecRepo.deleteAllById_CoachId(coach.getId());

        int prio = 0;
        for (String slug : safeList(specSlugs)) {
            int finalPrio = prio;
            specRepo.findBySlug(slug).ifPresent(spec -> {
                CoachSpecializationEntity csp = new CoachSpecializationEntity();
                csp.setId(new CoachSpecializationId(coach.getId(), spec.getId()));
                csp.setPriority(finalPrio);
                coachSpecRepo.save(csp);
            });
            prio++;
        }
    }

    private CoachProfileAggregate loadAggregate(CoachEntity coach) {
        var sports = coachSportRepo.findAllById_CoachIdOrderByPriorityAsc(coach.getId())
                .stream()
                .map(cs -> sportRepo.findById(cs.getId().getSportId()).map(s -> s.getSlug()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        var specs = coachSpecRepo.findAllById_CoachIdOrderByPriorityAsc(coach.getId())
                .stream()
                .map(csp -> specRepo.findById(csp.getId().getSpecializationId()).map(s -> s.getSlug()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new CoachProfileAggregate(
                coach.getId(),
                coach.getDisplayName(),
                coach.getSlug(),
                coach.getBio(),
                coach.getWebsiteUrl(),
                coach.getCity(),
                coach.isRemoteAvailable(),
                coach.isInPersonAvailable(),
                coach.getPriceMin(),
                coach.getPriceMax(),
                coach.getCurrency(),
                coach.getStatus(),
                sports,
                specs
        );
    }

    private static <T> List<T> safeList(List<T> in) {
        return in == null ? List.of() : in;
    }

    private static int prioHolder(int prio) {
        return prio;
    }

    @Override
    public void setStatus(UUID coachId, String status) {
        var coach = coachRepo.findById(coachId).orElseThrow();
        coach.setStatus(status);
        coach.setUpdatedAt(OffsetDateTime.now());
        coachRepo.save(coach);
    }
}
