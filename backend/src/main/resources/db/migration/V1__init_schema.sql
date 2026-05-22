-- ═══════════════════════════════════════════════════════════════════════════
-- V1__init_schema.sql  –  Schéma initial EduMarket
-- Géré par Flyway – NE PAS modifier manuellement
-- ═══════════════════════════════════════════════════════════════════════════

-- ── Extensions ──────────────────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ── Table : users ───────────────────────────────────────────────────────
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'STUDENT',  -- STUDENT | INSTRUCTOR | ADMIN
    is_premium    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Table : categories ──────────────────────────────────────────────────
CREATE TABLE categories (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(80)  NOT NULL UNIQUE,
    slug        VARCHAR(80)  NOT NULL UNIQUE,
    description TEXT,
    icon        VARCHAR(50)
);

-- ── Table : courses ─────────────────────────────────────────────────────
CREATE TABLE courses (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title          VARCHAR(200) NOT NULL,
    slug           VARCHAR(200) NOT NULL UNIQUE,
    description    TEXT,
    instructor_id  UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id    INTEGER      REFERENCES categories(id) ON DELETE SET NULL,
    price          NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    duration_hours NUMERIC(5,1),
    level          VARCHAR(20)  NOT NULL DEFAULT 'BEGINNER',  -- BEGINNER | INTERMEDIATE | ADVANCED
    language       VARCHAR(10)  NOT NULL DEFAULT 'fr',
    thumbnail_url  TEXT,
    is_published   BOOLEAN      NOT NULL DEFAULT FALSE,
    is_premium     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Table : enrollments ─────────────────────────────────────────────────
CREATE TABLE enrollments (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id      UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id    UUID      NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    progress     SMALLINT  NOT NULL DEFAULT 0 CHECK (progress BETWEEN 0 AND 100),
    enrolled_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP,
    UNIQUE (user_id, course_id)
);

-- ── Table : payments ────────────────────────────────────────────────────
CREATE TABLE payments (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id          UUID           NOT NULL REFERENCES users(id),
    course_id        UUID           REFERENCES courses(id),   -- NULL si abonnement
    amount           NUMERIC(10,2)  NOT NULL,
    currency         VARCHAR(3)     NOT NULL DEFAULT 'EUR',
    payment_method   VARCHAR(30)    NOT NULL,  -- CREDIT_CARD | PAYPAL | CRYPTO
    status           VARCHAR(20)    NOT NULL DEFAULT 'PENDING',  -- PENDING | SUCCESS | FAILED | REFUNDED
    provider_ref     VARCHAR(255),
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ── Table : subscriptions ───────────────────────────────────────────────
CREATE TABLE subscriptions (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id      UUID         NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    plan         VARCHAR(20)  NOT NULL DEFAULT 'MONTHLY',   -- MONTHLY | YEARLY | TRIAL
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',    -- ACTIVE | CANCELLED | EXPIRED
    started_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at   TIMESTAMP    NOT NULL,
    payment_id   UUID         REFERENCES payments(id)
);

-- ── Table : reviews ─────────────────────────────────────────────────────
CREATE TABLE reviews (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id    UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id  UUID      NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    rating     SMALLINT  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, course_id)
);

-- ── Index ────────────────────────────────────────────────────────────────
CREATE INDEX idx_courses_category       ON courses(category_id);
CREATE INDEX idx_courses_instructor     ON courses(instructor_id);
CREATE INDEX idx_courses_published      ON courses(is_published);
CREATE INDEX idx_enrollments_user       ON enrollments(user_id);
CREATE INDEX idx_enrollments_course     ON enrollments(course_id);
CREATE INDEX idx_payments_user          ON payments(user_id);
CREATE INDEX idx_reviews_course         ON reviews(course_id);
