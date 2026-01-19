-- CoachKompass - Initial schema (PostgreSQL)

-- UUID helper
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- ACCOUNT (Firebase-ready)
-- =========================
CREATE TABLE account (
                         id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         firebase_uid    VARCHAR(128) NOT NULL UNIQUE,
                         role            VARCHAR(30)  NOT NULL CHECK (role IN ('COACH', 'ADMIN')),
                         created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                         updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                         last_login_at   TIMESTAMPTZ  NULL
);

-- =========================
-- COACH
-- =========================
CREATE TABLE coach (
                       id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Optional: Coach kann später "geclaimed" werden via Firebase Account
                       account_id           UUID NULL UNIQUE REFERENCES account(id) ON DELETE SET NULL,

                       display_name         VARCHAR(200) NOT NULL,
                       slug                 VARCHAR(220) NOT NULL UNIQUE,

                       bio                  TEXT NULL,
                       website_url          VARCHAR(500) NULL,

    -- Location (MVP: text-based, später ggf. Geo)
                       city                 VARCHAR(120) NULL,
                       region               VARCHAR(120) NULL,
                       country              VARCHAR(2)   NULL, -- ISO-2 (DE, NL, ...)

                       remote_available     BOOLEAN NOT NULL DEFAULT TRUE,
                       in_person_available  BOOLEAN NOT NULL DEFAULT TRUE,

    -- Pricing
                       price_min            NUMERIC(10,2) NULL,
                       price_max            NUMERIC(10,2) NULL,
                       pricing_model        VARCHAR(30)   NULL CHECK (pricing_model IN ('HOURLY','MONTHLY','PACKAGE','ON_REQUEST')),
                       currency             VARCHAR(10)   NOT NULL DEFAULT 'EUR' CHECK (currency IN ('EUR','USD','CHF')),

                       status               VARCHAR(30)   NOT NULL DEFAULT 'DRAFT'
                           CHECK (status IN ('DRAFT','PUBLISHED','SUSPENDED')),

                       created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
                       updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- SOCIAL MEDIA LINKS
-- =========================
CREATE TABLE social_media_link (
                                   id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   coach_id       UUID NOT NULL REFERENCES coach(id) ON DELETE CASCADE,

                                   platform      VARCHAR(30) NOT NULL
                                       CHECK (platform IN ('INSTAGRAM','FACEBOOK','TIKTOK','YOUTUBE','X','LINKEDIN','TWITTER')),
                                   url           VARCHAR(500) NOT NULL,
                                   display_order INT NOT NULL DEFAULT 0,

                                   created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

                                   UNIQUE (coach_id, platform)
);

-- =========================
-- SPORT + JOIN
-- =========================
CREATE TABLE sport (
                       id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name  VARCHAR(120) NOT NULL UNIQUE,
                       slug  VARCHAR(140) NOT NULL UNIQUE
);

CREATE TABLE coach_sport (
                             coach_id   UUID NOT NULL REFERENCES coach(id) ON DELETE CASCADE,
                             sport_id   UUID NOT NULL REFERENCES sport(id) ON DELETE CASCADE,
                             priority   INT  NOT NULL DEFAULT 0,

                             PRIMARY KEY (coach_id, sport_id)
);

-- =========================
-- SPECIALIZATION + JOIN
-- =========================
CREATE TABLE specialization (
                                id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                name  VARCHAR(160) NOT NULL UNIQUE,
                                slug  VARCHAR(180) NOT NULL UNIQUE
);

CREATE TABLE coach_specialization (
                                      coach_id           UUID NOT NULL REFERENCES coach(id) ON DELETE CASCADE,
                                      specialization_id  UUID NOT NULL REFERENCES specialization(id) ON DELETE CASCADE,
                                      priority           INT  NOT NULL DEFAULT 0,

                                      PRIMARY KEY (coach_id, specialization_id)
);

-- =========================
-- TRUST VERIFICATION
-- =========================
CREATE TABLE trust_verification (
                                    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    coach_id     UUID NOT NULL REFERENCES coach(id) ON DELETE CASCADE,

                                    type        VARCHAR(40) NOT NULL
                                        CHECK (type IN ('LICENSE','CERTIFICATE','IDENTITY','CLUB_REFERENCE')),
                                    status      VARCHAR(40) NOT NULL DEFAULT 'PENDING'
                                        CHECK (status IN ('PENDING','APPROVED','REJECTED')),

                                    note        TEXT NULL,
                                    verified_at TIMESTAMPTZ NULL,

                                    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                                    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- MEDIA ASSET
-- =========================
CREATE TABLE media_asset (
                             id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             coach_id     UUID NOT NULL REFERENCES coach(id) ON DELETE CASCADE,

                             type        VARCHAR(20) NOT NULL CHECK (type IN ('IMAGE','VIDEO')),
                             url         VARCHAR(700) NOT NULL,

                             visibility  VARCHAR(30) NOT NULL DEFAULT 'PUBLIC'
                                 CHECK (visibility IN ('PUBLIC','PREMIUM_ONLY')),

                             created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- SUBSCRIPTION
-- =========================
CREATE TABLE subscription (
                              id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              coach_id                 UUID NOT NULL UNIQUE REFERENCES coach(id) ON DELETE CASCADE,

                              plan                    VARCHAR(30) NOT NULL CHECK (plan IN ('PREMIUM')),
                              status                  VARCHAR(30) NOT NULL CHECK (status IN ('ACTIVE','PAST_DUE','CANCELED')),
                              provider                VARCHAR(30) NOT NULL CHECK (provider IN ('STRIPE')),

                              provider_customer_id     VARCHAR(200) NULL,
                              provider_subscription_id VARCHAR(200) NULL,
                              current_period_end       TIMESTAMPTZ NULL,

                              created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
                              updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- LEAD CLICK (Stats)
-- =========================
CREATE TABLE lead_click (
                            id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            coach_id     UUID NOT NULL REFERENCES coach(id) ON DELETE CASCADE,

                            type        VARCHAR(40) NOT NULL CHECK (type IN ('WEBSITE_CLICK','CONTACT_CLICK')),
                            meta        JSONB NULL,

                            created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- INDEXES (Search/Filter)
-- =========================
CREATE INDEX idx_coach_status            ON coach(status);
CREATE INDEX idx_coach_city              ON coach(city);
CREATE INDEX idx_coach_remote            ON coach(remote_available);
CREATE INDEX idx_coach_in_person         ON coach(in_person_available);

CREATE INDEX idx_coach_sport_sport       ON coach_sport(sport_id);
CREATE INDEX idx_coach_spec_spec         ON coach_specialization(specialization_id);

CREATE INDEX idx_trust_coach_status      ON trust_verification(coach_id, status);
CREATE INDEX idx_media_coach_visibility  ON media_asset(coach_id, visibility);

CREATE INDEX idx_lead_click_coach_type   ON lead_click(coach_id, type);
