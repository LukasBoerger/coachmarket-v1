-- =========
-- Sports
-- =========
insert into sport (id, name, slug) values
                                       (gen_random_uuid(), 'Bodybuilding', 'bodybuilding'),
                                       (gen_random_uuid(), 'Handball', 'handball'),
                                       (gen_random_uuid(), 'Laufen', 'laufen'),
                                       (gen_random_uuid(), 'Fußball', 'fussball'),
                                       (gen_random_uuid(), 'CrossFit', 'crossfit'),
                                       (gen_random_uuid(), 'Fitness', 'fitness')
    on conflict do nothing;

-- ==================
-- Specializations
-- ==================
insert into specialization (id, name, slug) values
                                                (gen_random_uuid(), 'Athletik', 'athletik'),
                                                (gen_random_uuid(), 'Technik', 'technik'),
                                                (gen_random_uuid(), 'Wettkampfvorbereitung', 'wettkampfvorbereitung'),
                                                (gen_random_uuid(), 'Reha/Prävention', 'reha-praevention'),
                                                (gen_random_uuid(), 'Ernährung', 'ernaehrung'),
                                                (gen_random_uuid(), 'Hyrox', 'hyrox'),
                                                (gen_random_uuid(), 'Mobility', 'mobility'),
                                                (gen_random_uuid(), 'Kraftaufbau', 'kraftaufbau')
    on conflict do nothing;

-- =========
-- Coaches
-- =========
insert into coach (
    id, display_name, slug, bio, website_url,
    city, region, country,
    remote_available, in_person_available,
    price_min, price_max, pricing_model, currency,
    status, created_at, updated_at
) values
      (gen_random_uuid(), 'Max Kraft', 'max-kraft',
       'Bodybuilding Coach mit Fokus auf Hypertrophie, Progression und Ernährung.',
       'https://example.com/max-kraft',
       'Münster', 'NRW', 'DE',
       true, true,
       59.00, 149.00, 'PACKAGE', 'EUR',
       'PUBLISHED', now(), now()),
      (gen_random_uuid(), 'Anna Team', 'anna-team',
       'Handball Trainerin für Technik/Taktik – Team & Individual.',
       'https://example.com/anna-team',
       'Dortmund', 'NRW', 'DE',
       true, true,
       40.00, 90.00, 'HOURLY', 'EUR',
       'PUBLISHED', now(), now()),
      (gen_random_uuid(), 'Leo Run', 'leo-run',
       'Laufcoach: Marathon-Planung, FatMax, Pace-Strategie.',
       'https://example.com/leo-run',
       'Köln', 'NRW', 'DE',
       true, false,
       30.00, 120.00, 'MONTHLY', 'EUR',
       'PUBLISHED', now(), now()),
      (gen_random_uuid(), 'Mira Hybrid', 'mira-hybrid',
       'Hybrid Training (Hyrox/CrossFit) + Mobility/Regeneration.',
       'https://example.com/mira-hybrid',
       'Hamburg', 'HH', 'DE',
       true, true,
       49.00, 199.00, 'PACKAGE', 'EUR',
       'PUBLISHED', now(), now()),
      (gen_random_uuid(), 'Jonas Athletik', 'jonas-athletik',
       'Athletiktraining für Teamsport (Schnelligkeit, Sprungkraft, Prehab).',
       'https://example.com/jonas-athletik',
       'Berlin', 'BE', 'DE',
       true, true,
       60.00, 120.00, 'HOURLY', 'EUR',
       'PUBLISHED', now(), now())
    on conflict do nothing;

-- ==========================
-- Coach <-> Sport mapping
-- ==========================
insert into coach_sport (coach_id, sport_id, priority)
select c.id, s.id, 1
from coach c
         join sport s on (
    (c.slug = 'max-kraft' and s.slug in ('bodybuilding','fitness'))
        or (c.slug = 'anna-team' and s.slug in ('handball'))
        or (c.slug = 'leo-run' and s.slug in ('laufen'))
        or (c.slug = 'mira-hybrid' and s.slug in ('crossfit','fitness'))
        or (c.slug = 'jonas-athletik' and s.slug in ('handball','fussball'))
    )
    on conflict do nothing;

-- =================================
-- Coach <-> Specialization mapping
-- =================================
insert into coach_specialization (coach_id, specialization_id, priority)
select c.id, sp.id, 1
from coach c
         join specialization sp on (
    (c.slug = 'max-kraft' and sp.slug in ('kraftaufbau','ernaehrung','wettkampfvorbereitung'))
        or (c.slug = 'anna-team' and sp.slug in ('technik','athletik'))
        or (c.slug = 'leo-run' and sp.slug in ('wettkampfvorbereitung','reha-praevention'))
        or (c.slug = 'mira-hybrid' and sp.slug in ('hyrox','mobility','athletik'))
        or (c.slug = 'jonas-athletik' and sp.slug in ('athletik','reha-praevention'))
    )
    on conflict do nothing;

-- ==================
-- Optional: Socials
-- ==================
insert into social_media_link (id, coach_id, platform, url, display_order, created_at)
select gen_random_uuid(), c.id, 'INSTAGRAM', 'https://instagram.com/' || c.slug, 0, now()
from coach c
    on conflict do nothing;
