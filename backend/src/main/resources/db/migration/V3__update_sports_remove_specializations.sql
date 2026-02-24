-- ========================
-- Sportarten bereinigen
-- ========================
DELETE FROM coach_sport;
DELETE FROM sport;

INSERT INTO sport (id, name, slug) VALUES
                                       (gen_random_uuid(), 'Laufen',              'laufen'),
                                       (gen_random_uuid(), 'Fitness',             'fitness'),
                                       (gen_random_uuid(), 'Krafttraining',       'krafttraining'),
                                       (gen_random_uuid(), 'Bodybuilding',        'bodybuilding'),
                                       (gen_random_uuid(), 'Hyrox / Functional',  'hyrox'),
                                       (gen_random_uuid(), 'Yoga',                'yoga'),
                                       (gen_random_uuid(), 'Radsport',            'radsport'),
                                       (gen_random_uuid(), 'Schwimmen',           'schwimmen'),
                                       (gen_random_uuid(), 'Kampfsport',          'kampfsport'),
                                       (gen_random_uuid(), 'Teamsport',           'teamsport'),
                                       (gen_random_uuid(), 'Triathlon',           'triathlon'),
                                       (gen_random_uuid(), 'Handball',            'handball')
    ON CONFLICT DO NOTHING;

-- Seed-Coaches neu verknüpfen
INSERT INTO coach_sport (coach_id, sport_id, priority)
SELECT c.id, s.id, 1
FROM coach c
         JOIN sport s ON (
    (c.slug = 'max-kraft'      AND s.slug IN ('krafttraining', 'bodybuilding'))
        OR (c.slug = 'anna-team'      AND s.slug IN ('handball', 'teamsport'))
        OR (c.slug = 'leo-run'        AND s.slug IN ('laufen'))
        OR (c.slug = 'mira-hybrid'    AND s.slug IN ('hyrox', 'fitness'))
        OR (c.slug = 'jonas-athletik' AND s.slug IN ('teamsport', 'handball'))
    )
    ON CONFLICT DO NOTHING;

-- ========================
-- Spezialisierungen leeren
-- ========================
DELETE FROM coach_specialization;
DELETE FROM specialization;