package de.coachkompass.backend.infrastructure.coach;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoachCrudRepository extends JpaRepository<CoachEntity, UUID> {
    Optional<CoachEntity> findBySlug(String slug);
    List<CoachEntity> findByStatus(String status);

    @Query(value = """
        select
          c.id as id,
          c.display_name as displayName,
          c.slug as slug,
          c.bio as bio,
          c.website_url as websiteUrl,
          c.city as city,
          c.remote_available as remoteAvailable,
          c.in_person_available as inPersonAvailable,
          c.price_min as priceMin,
          c.price_max as priceMax,
          c.currency as currency,
          c.status as status
        from coach c
        where c.status = 'PUBLISHED'
          and (:remote is null or c.remote_available = :remote)
          and (:inPerson is null or c.in_person_available = :inPerson)
          and (:city is null or lower(c.city) = lower(:city))
          and (:priceMax is null or c.price_min <= :priceMax)
          and (
            :sportSlug is null
            or exists (
              select 1
              from coach_sport cs
              join sport s on s.id = cs.sport_id
              where cs.coach_id = c.id and s.slug = :sportSlug
            )
          )
          and (
            :specializationSlug is null
            or exists (
              select 1
              from coach_specialization csp
              join specialization sp on sp.id = csp.specialization_id
              where csp.coach_id = c.id and sp.slug = :specializationSlug
            )
          )
        order by c.display_name asc
        """, nativeQuery = true)
    List<CoachRow> searchPublished(
            @Param("sportSlug") String sportSlug,
            @Param("specializationSlug") String specializationSlug,
            @Param("remote") Boolean remote,
            @Param("inPerson") Boolean inPerson,
            @Param("city") String city,
            @Param("priceMax") BigDecimal priceMax
    );
}
