package de.coachkompass.backend.domain.myprofile;

import de.coachkompass.backend.application.myprofile.MyCoachProfileDto;
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

    public Optional<MyCoachProfileDto> getMyProfile(String firebaseUid) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);

        return repo.findByAccountId(account.getId())
                .map(this::toDto);
    }

    @Transactional
    public MyCoachProfileDto upsertMyProfile(String firebaseUid, MyCoachProfileDto dto) {
        var account = accountService.findOrCreateCoachAccount(firebaseUid);

        var aggregate = new MyCoachProfileRepository.CoachProfileAggregate(
                null,
                dto.displayName(),
                dto.slug(), // wird infra ggf. überschreiben beim Create
                dto.bio(),
                dto.websiteUrl(),
                dto.city(),
                dto.remoteAvailable(),
                dto.inPersonAvailable(),
                dto.priceMin(),
                dto.priceMax(),
                dto.currency(),
                dto.status(), // wird infra beim Create auf DRAFT setzen
                dto.sportSlugs(),
                dto.specializationSlugs()
        );

        return toDto(repo.upsert(account.getId(), aggregate));
    }

    private MyCoachProfileDto toDto(MyCoachProfileRepository.CoachProfileAggregate a) {
        return new MyCoachProfileDto(
                a.displayName(),
                a.bio(),
                a.websiteUrl(),
                a.city(),
                a.remoteAvailable(),
                a.inPersonAvailable(),
                a.priceMin(),
                a.priceMax(),
                a.currency(),
                a.sportSlugs(),
                a.specializationSlugs(),
                a.status(),
                a.slug()
        );
    }
}
