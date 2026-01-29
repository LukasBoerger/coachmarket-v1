package de.coachkompass.backend.infrastructure.myprofile;

import de.coachkompass.backend.domain.myprofile.MyCoachProfileRepository;
import de.coachkompass.backend.domain.util.SlugUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class MyCoachProfileRepositoryImpl implements MyCoachProfileRepository {

    private final JdbcTemplate jdbc;

    public JdbcMyCoachProfileRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<CoachProfileAggregate> findByAccountId(UUID accountId) {
        var baseRows = jdbc.query("""
                select id, display_name, slug, bio, website_url, city,
                       remote_available, in_person_available,
                       price_min, price_max, currency, status
                from coach
                where account_id = ?
                """, (rs, rowNum) -> mapCoach(rs), accountId);

        if (baseRows.isEmpty()) return Optional.empty();

        var base = baseRows.get(0);
        UUID coachId = base.coachId();

        var sportSlugs = jdbc.queryForList("""
                select s.slug
                from coach_sport cs
                join sport s on s.id = cs.sport_id
                where cs.coach_id = ?
                order by cs.priority asc
                """, String.class, coachId);

        var specSlugs = jdbc.queryForList("""
                select sp.slug
                from coach_specialization csp
                join specialization sp on sp.id = csp.specialization_id
                where csp.coach_id = ?
                order by csp.priority asc
                """, String.class, coachId);

        return Optional.of(new CoachProfileAggregate(
                coachId,
                base.displayName(),
                base.slug(),
                base.bio(),
                base.websiteUrl(),
                base.city(),
                base.remoteAvailable(),
                base.inPersonAvailable(),
                base.priceMin(),
                base.priceMax(),
                base.currency(),
                base.status(),
                sportSlugs == null ? List.of() : sportSlugs,
                specSlugs == null ? List.of() : specSlugs
        ));
    }

    @Override
    @Transactional
    public CoachProfileAggregate upsert(UUID accountId, CoachProfileAggregate aggregate) {
        UUID coachId = findCoachIdByAccount(accountId)
                .orElseGet(() -> createCoachSkeleton(accountId, aggregate.displayName(), aggregate.currency()));

        // Update coach main
        jdbc.update("""
                update coach
                set display_name = ?,
                    bio = ?,
                    website_url = ?,
                    city = ?,
                    remote_available = ?,
                    in_person_available = ?,
                    price_min = ?,
                    price_max = ?,
                    currency = ?,
                    updated_at = now()
                where id = ?
                """,
                aggregate.displayName(),
                aggregate.bio(),
                aggregate.websiteUrl(),
                aggregate.city(),
                aggregate.remoteAvailable(),
                aggregate.inPersonAvailable(),
                aggregate.priceMin(),
                aggregate.priceMax(),
                aggregate.currency(),
                coachId
        );

        replaceCoachSports(coachId, aggregate.sportSlugs());
        replaceCoachSpecializations(coachId, aggregate.specializationSlugs());

        return findByCoachId(coachId).orElseThrow();
    }

    // ---- internals ----

    private Optional<UUID> findCoachIdByAccount(UUID accountId) {
        List<UUID> ids = jdbc.queryForList("select id from coach where account_id = ?", UUID.class, accountId);
        return ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
    }

    private UUID createCoachSkeleton(UUID accountId, String displayName, String currency) {
        UUID id = UUID.randomUUID();

        String slugBase = SlugUtil.slugify(displayName);
        String slug = ensureUniqueSlug(slugBase);

        jdbc.update("""
                insert into coach (
                    id, account_id, display_name, slug,
                    remote_available, in_person_available,
                    currency, status, created_at, updated_at
                ) values (?, ?, ?, ?, true, true, ?, 'DRAFT', now(), now())
                """, id, accountId, displayName, slug, currency == null ? "EUR" : currency);

        return id;
    }

    private String ensureUniqueSlug(String slugBase) {
        String candidate = slugBase;
        int i = 2;
        while (Boolean.TRUE.equals(jdbc.queryForObject(
                "select exists(select 1 from coach where slug = ?)",
                Boolean.class,
                candidate
        ))) {
            candidate = slugBase + "-" + i++;
        }
        return candidate;
    }

    private void replaceCoachSports(UUID coachId, List<String> sportSlugs) {
        jdbc.update("delete from coach_sport where coach_id = ?", coachId);

        int prio = 0;
        for (String slug : sportSlugs == null ? List.<String>of() : sportSlugs) {
            UUID sportId = jdbc.queryForObject("select id from sport where slug = ?", UUID.class, slug);
            if (sportId != null) {
                jdbc.update("""
                        insert into coach_sport (coach_id, sport_id, priority)
                        values (?, ?, ?)
                        """, coachId, sportId, prio++);
            }
        }
    }

    private void replaceCoachSpecializations(UUID coachId, List<String> specSlugs) {
        jdbc.update("delete from coach_specialization where coach_id = ?", coachId);

        int prio = 0;
        for (String slug : specSlugs == null ? List.<String>of() : specSlugs) {
            UUID specId = jdbc.queryForObject("select id from specialization where slug = ?", UUID.class, slug);
            if (specId != null) {
                jdbc.update("""
                        insert into coach_specialization (coach_id, specialization_id, priority)
                        values (?, ?, ?)
                        """, coachId, specId, prio++);
            }
        }
    }

    private Optional<CoachProfileAggregate> findByCoachId(UUID coachId) {
        var baseRows = jdbc.query("""
                select id, display_name, slug, bio, website_url, city,
                       remote_available, in_person_available,
                       price_min, price_max, currency, status
                from coach
                where id = ?
                """, (rs, rowNum) -> mapCoach(rs), coachId);

        if (baseRows.isEmpty()) return Optional.empty();

        var base = baseRows.get(0);

        var sportSlugs = jdbc.queryForList("""
                select s.slug
                from coach_sport cs
                join sport s on s.id = cs.sport_id
                where cs.coach_id = ?
                order by cs.priority asc
                """, String.class, coachId);

        var specSlugs = jdbc.queryForList("""
                select sp.slug
                from coach_specialization csp
                join specialization sp on sp.id = csp.specialization_id
                where csp.coach_id = ?
                order by csp.priority asc
                """, String.class, coachId);

        return Optional.of(new CoachProfileAggregate(
                coachId,
                base.displayName(),
                base.slug(),
                base.bio(),
                base.websiteUrl(),
                base.city(),
                base.remoteAvailable(),
                base.inPersonAvailable(),
                base.priceMin(),
                base.priceMax(),
                base.currency(),
                base.status(),
                sportSlugs == null ? List.of() : sportSlugs,
                specSlugs == null ? List.of() : specSlugs
        ));
    }

    private CoachRow mapCoach(ResultSet rs) throws java.sql.SQLException {
        return new CoachRow(
                UUID.fromString(rs.getObject("id").toString()),
                rs.getString("display_name"),
                rs.getString("slug"),
                rs.getString("bio"),
                rs.getString("website_url"),
                rs.getString("city"),
                rs.getBoolean("remote_available"),
                rs.getBoolean("in_person_available"),
                rs.getBigDecimal("price_min"),
                rs.getBigDecimal("price_max"),
                rs.getString("currency"),
                rs.getString("status")
        );
    }

    private record CoachRow(
            UUID coachId,
            String displayName,
            String slug,
            String bio,
            String websiteUrl,
            String city,
            boolean remoteAvailable,
            boolean inPersonAvailable,
            java.math.BigDecimal priceMin,
            java.math.BigDecimal priceMax,
            String currency,
            String status
    ) {}
}
