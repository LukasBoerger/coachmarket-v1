package de.coachkompass.backend.domain.myprofile;

import de.coachkompass.backend.domain.account.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MyCoachProfileService {

    private final AccountService accountService;
    private final MyCoachProfileRepository repo;

    public MyCoachProfileService(AccountService accountService, MyCoachProfileRepository repo) {
        this.accountService = accountService;
        this.repo = repo;
    }

    public Optional<CoachProfile> getMyProfile(String firebaseUid) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);
        return repo.findByAccountId(account.getId());
    }

    @Transactional
    public CoachProfile upsertMyProfile(String firebaseUid, CoachProfile profile) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);
        return repo.upsert(account.getId(), profile);
    }

    @Transactional
    public CoachProfile publishMyProfile(String firebaseUid) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);
        var profile = repo.findByAccountId(account.getId())
                .orElseThrow(() -> new IllegalStateException("No coach profile found. Save profile first."));
        repo.setStatus(profile.coachId(), "PUBLISHED");
        return repo.findByAccountId(account.getId()).orElseThrow();
    }

    @Transactional
    public CoachProfile unpublishMyProfile(String firebaseUid) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);
        var profile = repo.findByAccountId(account.getId())
                .orElseThrow(() -> new IllegalStateException("No coach profile found."));
        repo.setStatus(profile.coachId(), "DRAFT");
        return repo.findByAccountId(account.getId()).orElseThrow();
    }
}