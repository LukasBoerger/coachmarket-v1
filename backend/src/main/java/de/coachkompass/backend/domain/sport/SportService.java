package de.coachkompass.backend.domain.sport;

import de.coachkompass.backend.application.sport.SportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SportService {

    private final SportRepository sportRepo;

    public SportService(SportRepository sportRepo) {
        this.sportRepo = sportRepo;
    }

    public List<Sport> listAll(){
        return this.sportRepo.listPublished();
    }

    public Optional<Sport> getBySlug(String slug) {
        return this.sportRepo.getBySlug(slug);
    }
}
