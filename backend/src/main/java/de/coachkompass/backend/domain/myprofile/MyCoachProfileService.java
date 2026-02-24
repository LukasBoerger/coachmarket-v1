package de.coachkompass.backend.domain.myprofile;

import de.coachkompass.backend.application.myprofile.MyCoachProfileDto;
import de.coachkompass.backend.domain.account.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class MyCoachProfileService {

    private final AccountService accountService;
    private final MyCoachProfileRepository repo;

    public MyCoachProfileService(AccountService accountService, MyCoachProfileRepository repo) {
        this.accountService = accountService;
        this.repo = repo;
    }

    public Optional<MyCoachProfileDto> getMyProfile(String firebaseUid) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);
        return repo.findByAccountId(account.getId()).map(this::toDto);
    }

    @Transactional
    public MyCoachProfileDto upsertMyProfile(String firebaseUid, MyCoachProfileDto dto) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);
        var aggregate = new MyCoachProfileRepository.CoachProfileAggregate(
                null,
                dto.displayName(), dto.slug(),
                dto.bio(), dto.websiteUrl(),
                dto.city(), dto.region(), dto.country(),
                dto.remoteAvailable(), dto.inPersonAvailable(),
                dto.priceMin(), dto.priceMax(), dto.pricingModel(),
                dto.currency(), dto.status(),
                dto.sportSlugs(), dto.specializationSlugs(),
                dto.socialLinks()
        );
        return toDto(repo.upsert(account.getId(), aggregate));
    }

    @Transactional
    public MyCoachProfileDto publishMyProfile(String firebaseUid) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);
        var agg = repo.findByAccountId(account.getId())
                .orElseThrow(() -> new IllegalStateException("No coach profile found. Save profile first."));
        repo.setStatus(agg.coachId(), "PUBLISHED");
        return toDto(repo.findByAccountId(account.getId()).orElseThrow());
    }

    @Transactional
    public MyCoachProfileDto unpublishMyProfile(String firebaseUid) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);
        var agg = repo.findByAccountId(account.getId())
                .orElseThrow(() -> new IllegalStateException("No coach profile found."));
        repo.setStatus(agg.coachId(), "DRAFT");
        return toDto(repo.findByAccountId(account.getId()).orElseThrow());
    }

    public UUID getCoachId(String firebaseUid) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);
        return repo.findByAccountId(account.getId())
                .map(MyCoachProfileRepository.CoachProfileAggregate::coachId)
                .orElseThrow(() -> new IllegalStateException("No coach profile found"));
    }

    private MyCoachProfileDto toDto(MyCoachProfileRepository.CoachProfileAggregate a) {
        return new MyCoachProfileDto(
                a.displayName(), a.bio(), a.websiteUrl(),
                a.city(), a.region(), a.country(),
                a.remoteAvailable(), a.inPersonAvailable(),
                a.priceMin(), a.priceMax(), a.pricingModel(),
                a.currency(),
                a.sportSlugs(), a.specializationSlugs(),
                a.socialLinks(),
                a.status(), a.slug()
        );
    }
}