-- ═══════════════════════════════════════════════════════════════════════════
-- V2__seed_data.sql  –  Données de démonstration EduMarket
-- ═══════════════════════════════════════════════════════════════════════════

-- ── Catégories ───────────────────────────────────────────────────────────
INSERT INTO categories (name, slug, description, icon) VALUES
  ('Développement Web',   'dev-web',      'HTML, CSS, JavaScript, frameworks modernes', 'code'),
  ('Data Science',        'data-science', 'Python, Machine Learning, IA, statistiques',  'chart-bar'),
  ('Design UX/UI',        'design-ux-ui', 'Figma, prototypage, recherche utilisateur',   'palette'),
  ('DevOps & Cloud',      'devops-cloud', 'Docker, Kubernetes, CI/CD, AWS, GCP',         'cloud'),
  ('Cybersécurité',       'cybersec',     'Sécurité réseau, ethical hacking, OWASP',     'shield'),
  ('Mobile',              'mobile',       'Flutter, React Native, Swift, Kotlin',         'mobile'),
  ('Gestion de Projet',   'gestion-projet','Agile, Scrum, Kanban, PMP',                  'clipboard');

-- ── Instructeurs (mot de passe: Admin1234! – bcrypt) ─────────────────────
INSERT INTO users (id, email, password_hash, full_name, role) VALUES
  ('a0000000-0000-0000-0000-000000000001', 'alice@edumarket.io',
   '$2a$12$LnEXmXVXaQi7dq3A5GV3.u8z9j2P1oQ7K4RnSd6tFkNm5JwYhV6Oi',
   'Alice Dupont', 'INSTRUCTOR'),
  ('a0000000-0000-0000-0000-000000000002', 'bob@edumarket.io',
   '$2a$12$LnEXmXVXaQi7dq3A5GV3.u8z9j2P1oQ7K4RnSd6tFkNm5JwYhV6Oi',
   'Bob Martin',   'INSTRUCTOR'),
  ('a0000000-0000-0000-0000-000000000003', 'carla@edumarket.io',
   '$2a$12$LnEXmXVXaQi7dq3A5GV3.u8z9j2P1oQ7K4RnSd6tFkNm5JwYhV6Oi',
   'Carla Rossi',  'INSTRUCTOR');

-- ── Étudiants ────────────────────────────────────────────────────────────
INSERT INTO users (id, email, password_hash, full_name, role, is_premium) VALUES
  ('b0000000-0000-0000-0000-000000000001', 'etudiant1@example.com',
   '$2a$12$LnEXmXVXaQi7dq3A5GV3.u8z9j2P1oQ7K4RnSd6tFkNm5JwYhV6Oi',
   'Jean-Paul Tremblay', 'STUDENT', TRUE),
  ('b0000000-0000-0000-0000-000000000002', 'etudiant2@example.com',
   '$2a$12$LnEXmXVXaQi7dq3A5GV3.u8z9j2P1oQ7K4RnSd6tFkNm5JwYhV6Oi',
   'Sophie Leblanc',     'STUDENT', FALSE),
  ('b0000000-0000-0000-0000-000000000003', 'etudiant3@example.com',
   '$2a$12$LnEXmXVXaQi7dq3A5GV3.u8z9j2P1oQ7K4RnSd6tFkNm5JwYhV6Oi',
   'Marc Gagnon',        'STUDENT', TRUE);

-- ── Cours ─────────────────────────────────────────────────────────────────
INSERT INTO courses (id, title, slug, description, instructor_id, category_id, price, duration_hours, level, is_published, is_premium) VALUES
  ('c0000000-0000-0000-0000-000000000001',
   'Angular 17 – De zéro à expert', 'angular-17-zero-expert',
   'Maîtrisez Angular 17 avec Signals, Standalone Components et Server-Side Rendering.',
   'a0000000-0000-0000-0000-000000000001', 1, 49.99, 32.5, 'INTERMEDIATE', TRUE, FALSE),

  ('c0000000-0000-0000-0000-000000000002',
   'Spring Boot 3 & Java 21', 'spring-boot-3-java-21',
   'Construisez des APIs REST robustes avec Virtual Threads et GraalVM Native.',
   'a0000000-0000-0000-0000-000000000002', 1, 59.99, 28.0, 'INTERMEDIATE', TRUE, FALSE),

  ('c0000000-0000-0000-0000-000000000003',
   'Machine Learning avec Python', 'machine-learning-python',
   'Pandas, Scikit-Learn, TensorFlow : devenez Data Scientist opérationnel.',
   'a0000000-0000-0000-0000-000000000003', 2, 79.99, 45.0, 'ADVANCED', TRUE, TRUE),

  ('c0000000-0000-0000-0000-000000000004',
   'Docker & Kubernetes en Production', 'docker-kubernetes-production',
   'Orchestrez vos conteneurs et déployez sur un cluster Kubernetes réel.',
   'a0000000-0000-0000-0000-000000000002', 4, 69.99, 22.0, 'ADVANCED', TRUE, TRUE),

  ('c0000000-0000-0000-0000-000000000005',
   'UX Design – Fondamentaux', 'ux-design-fondamentaux',
   'Comprenez vos utilisateurs et concevez des interfaces intuitives avec Figma.',
   'a0000000-0000-0000-0000-000000000001', 3, 39.99, 18.5, 'BEGINNER', TRUE, FALSE),

  ('c0000000-0000-0000-0000-000000000006',
   'Ethical Hacking & Pentesting', 'ethical-hacking-pentesting',
   'Apprenez à sécuriser vos systèmes en pensant comme un attaquant.',
   'a0000000-0000-0000-0000-000000000003', 5, 89.99, 38.0, 'ADVANCED', TRUE, TRUE),

  ('c0000000-0000-0000-0000-000000000007',
   'Flutter – Applications Cross-Platform', 'flutter-cross-platform',
   'Développez des apps iOS et Android avec un seul codebase Dart/Flutter.',
   'a0000000-0000-0000-0000-000000000001', 6, 54.99, 26.0, 'INTERMEDIATE', TRUE, FALSE),

  ('c0000000-0000-0000-0000-000000000008',
   'Scrum Master Certifié', 'scrum-master-certifie',
   'Préparez votre certification PSM I avec des cas pratiques et des simulations.',
   'a0000000-0000-0000-0000-000000000002', 7, 44.99, 15.0, 'BEGINNER', TRUE, FALSE);

-- ── Paiements ─────────────────────────────────────────────────────────────
INSERT INTO payments (id, user_id, course_id, amount, payment_method, status) VALUES
  ('d0000000-0000-0000-0000-000000000001',
   'b0000000-0000-0000-0000-000000000001',
   'c0000000-0000-0000-0000-000000000001',
   49.99, 'CREDIT_CARD', 'SUCCESS'),
  ('d0000000-0000-0000-0000-000000000002',
   'b0000000-0000-0000-0000-000000000002',
   'c0000000-0000-0000-0000-000000000005',
   39.99, 'PAYPAL', 'SUCCESS'),
  ('d0000000-0000-0000-0000-000000000003',
   'b0000000-0000-0000-0000-000000000003',
   NULL,
   149.99, 'CREDIT_CARD', 'SUCCESS');

-- ── Abonnements ───────────────────────────────────────────────────────────
INSERT INTO subscriptions (user_id, plan, status, started_at, expires_at, payment_id) VALUES
  ('b0000000-0000-0000-0000-000000000001', 'MONTHLY', 'ACTIVE',
   NOW(), NOW() + INTERVAL '30 days', 'd0000000-0000-0000-0000-000000000003'),
  ('b0000000-0000-0000-0000-000000000003', 'YEARLY',  'ACTIVE',
   NOW(), NOW() + INTERVAL '365 days', 'd0000000-0000-0000-0000-000000000003');

-- ── Inscriptions ──────────────────────────────────────────────────────────
INSERT INTO enrollments (user_id, course_id, progress) VALUES
  ('b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 65),
  ('b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000003', 20),
  ('b0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000005', 90),
  ('b0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000002', 45),
  ('b0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000004', 10);

-- ── Avis ──────────────────────────────────────────────────────────────────
INSERT INTO reviews (user_id, course_id, rating, comment) VALUES
  ('b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001',
   5, 'Excellente formation ! Les explications sur les Signals sont limpides.'),
  ('b0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000005',
   4, 'Très bien structuré, Figma bien expliqué. Manque de cas pratiques avancés.'),
  ('b0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000002',
   5, 'Java 21 Virtual Threads, enfin une vraie explication claire. Bravo !');
