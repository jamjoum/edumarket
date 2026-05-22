# 📚 EduMarket — Plateforme de Cours en Ligne

> Architecture de démonstration complète : **Spring Boot 3 · Java 21 · Angular 17 · Docker · Design Patterns**

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-DD0031?logo=angular&logoColor=white)](https://angular.dev)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📋 Table des matières

- [Vue d'ensemble](#-vue-densemble)
- [Architecture Générale](#-architecture-générale)
- [Démarrage Rapide](#-démarrage-rapide)
- [Backend – Spring Boot & Java 21](#-backend--spring-boot--java-21)
- [Frontend – Angular 17 & Signals](#-frontend--angular-17--signals)
- [Design Patterns](#-design-patterns)
- [Base de données & Flyway](#-base-de-données--flyway)
- [DevOps – Docker & Codespaces](#-devops--docker--codespaces)
- [API Reference](#-api-reference)
- [Structure du Projet](#-structure-du-projet)

---

## 🎯 Vue d'ensemble

**EduMarket** est une plateforme de cours en ligne qui illustre une architecture moderne full-stack :

| Fonctionnalité | Technologie |
|---|---|
| Catalogue de cours avec filtres | Angular 17 Signals |
| Achat à l'unité (CB / PayPal / Crypto) | Strategy Pattern |
| Abonnement Premium mensuel / annuel | Factory Pattern |
| Cache transparent des requêtes | Proxy Pattern |
| API REST documentée | Spring Boot + SpringDoc |
| Migrations DB automatiques | Flyway |
| Environnement reproductible | Docker Compose + Codespaces |

---

## 🏗 Architecture Générale

```
┌─────────────────────────────────────────────────────────────────┐
│                        NAVIGATEUR                                │
│                    Angular 17 (SPA)                             │
│          Signals · Standalone Components · Lazy Loading         │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTP / REST (JSON)
                    ┌───────▼────────┐
                    │  Nginx Proxy   │  :4200 (dev) / :80 (prod)
                    │  /api/* → :8080│
                    └───────┬────────┘
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                    Spring Boot 3.3                               │
│                      Java 21 JVM                                │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────┐    │
│  │  Controllers │  │   Services   │  │  Design Patterns   │    │
│  │  (MVC – C)   │→ │  (MVC – M)   │  │  Strategy / Proxy  │    │
│  └──────────────┘  └──────┬───────┘  │  Factory / Singleton│    │
│                           │          └────────────────────┘    │
│  ┌────────────────────────▼──────────────────────────────────┐  │
│  │              Spring Data JPA + Hibernate                   │  │
│  │              Virtual Threads (Project Loom)                │  │
│  └────────────────────────┬──────────────────────────────────┘  │
└───────────────────────────┼─────────────────────────────────────┘
                            │ JDBC
                    ┌───────▼────────┐
                    │  PostgreSQL 16 │
                    │                │
                    │  Flyway :       │
                    │  V1 – Schema   │
                    │  V2 – Seed     │
                    └────────────────┘
```

---

## 🚀 Démarrage Rapide

### Prérequis

| Outil | Version minimum |
|---|---|
| Docker Desktop | 24+ |
| Docker Compose | v2.20+ |
| Java (optionnel, dev local) | 21+ |
| Node.js (optionnel, dev local) | 20+ |

### ▶️ Lancement en une commande

```bash
# Cloner le projet
git clone https://github.com/votre-org/edumarket.git
cd edumarket

# Démarrer toute la stack
docker compose up --build
```

| Service | URL |
|---|---|
| 🌐 Frontend Angular | http://localhost:4200 |
| 🔌 API Spring Boot | http://localhost:8080/api/v1 |
| 📖 Swagger UI | http://localhost:8080/swagger-ui.html |
| 🐘 PostgreSQL | localhost:5432 |
| 🛠 PGAdmin | `docker compose --profile tools up` → http://localhost:5050 |

### 🔧 Développement local (sans Docker)

**Backend :**
```bash
# Démarrer uniquement PostgreSQL
docker compose up -d postgres

cd backend
mvn spring-boot:run
# → http://localhost:8080
```

**Frontend :**
```bash
cd frontend
npm install
npm start
# → http://localhost:4200
```

### ☁️ GitHub Codespaces

```
1. Ouvrir le dépôt sur GitHub
2. Code → Codespaces → Create Codespace
3. Le script .devcontainer/setup.sh s'exécute automatiquement
4. Les ports 4200 et 8080 sont forwardés dans le navigateur
```

---

## 🔧 Backend – Spring Boot & Java 21

### Nouveautés Java 21 utilisées

#### 1. Virtual Threads (Project Loom)

```yaml
# application.yml – activation globale
spring:
  threads:
    virtual:
      enabled: true
```

Chaque requête HTTP s'exécute sur un **Virtual Thread** léger :
- Pas de thread pool à dimensionner
- Bloquage I/O sans saturation de threads OS
- Idéal pour les applications web fortement concurrentes

```java
// Démonstration – 10 000 virtual threads simultanés
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10_000; i++) {
        executor.submit(() -> {
            Thread.sleep(10); // bloque le VT, pas un thread OS
            return processRequest();
        });
    }
}
```

#### 2. Switch Expressions

```java
// PaymentContext.java – résolution de la stratégie
String key = switch (method) {
    case CREDIT_CARD -> "CREDIT_CARD";
    case PAYPAL      -> "PAYPAL";
    case CRYPTO      -> "CRYPTO";
};

// SubscriptionFactory.java – calcul de la durée
LocalDateTime expiresAt = switch (plan) {
    case MONTHLY -> now.plusMonths(1);
    case YEARLY  -> now.plusYears(1);
    case TRIAL   -> now.plusDays(trialDays);
};
```

#### 3. Records (DTOs immutables)

```java
// CourseDTO.java
public record Response(
    UUID          id,
    String        title,
    BigDecimal    price,
    boolean       premium,
    LocalDateTime createdAt
) {}

// Utilisation élégante
var dto = new CourseDTO.Response(course.getId(), course.getTitle(), ...);
```

#### 4. Pattern Matching (instanceof)

```java
// Traitement polymorphique sans cast explicite
if (payment instanceof SubscriptionPayment sp) {
    activateSubscription(sp.getPlan());
}
```

### Couches de l'application

```
com.edumarket/
├── controller/          ← MVC : reçoit HTTP, délègue au service
├── service/             ← Logique métier, orchestration
├── repository/          ← Spring Data JPA, accès DB
├── model/               ← Entités JPA (@Entity)
├── dto/                 ← Records Java 21 (immutables)
├── payment/             ← Pattern Strategy (CB, PayPal, Crypto)
├── factory/             ← Pattern Factory (Subscription)
├── proxy/               ← Pattern Proxy (cache + audit)
├── config/              ← Security, CORS, OpenAPI
└── exception/           ← GlobalExceptionHandler (RFC 7807)
```

---

## 🎨 Frontend – Angular 17 & Signals

### Architecture Signals

Angular 17 introduit les **Signals** comme nouveau primitif réactif :

```typescript
// CourseService – état géré par Signals
@Injectable({ providedIn: 'root' })
export class CourseService {

  // Signal mutable (état brut)
  private readonly _courses = signal<CourseSummary[]>([]);

  // Filtres réactifs
  readonly levelFilter   = signal<CourseLevel | null>(null);
  readonly premiumFilter = signal<boolean | null>(null);

  // Computed Signal : valeur dérivée automatiquement mémoïsée
  readonly courses = computed(() => {
    let list = this._courses();
    const level = this.levelFilter();
    if (level) list = list.filter(c => c.level === level);
    return list;
  });

  // Compteur dérivé
  readonly coursesCount = computed(() => this.courses().length);
}
```

**Dans le template (sans `async` pipe) :**

```html
<!-- lecture directe du Signal avec () -->
<p>{{ courseService.coursesCount() }} cours</p>

@for (course of courseService.courses(); track course.id) {
  <app-course-card [course]="course" />
}
```

### Composants Standalone (sans NgModule)

```typescript
@Component({
  selector: 'app-catalog',
  standalone: true,                            // ← Pas de NgModule
  imports: [CommonModule, RouterLink],         // ← Imports directs
  changeDetection: ChangeDetectionStrategy.OnPush,  // ← Optimisé Signals
})
export class CatalogComponent { ... }
```

### Architecture par Services (Singleton)

```
AuthService     → état utilisateur (Signal currentUser)
CourseService   → catalogue + filtres (Signals)
PaymentService  → traitement paiements (Signal processing)
```

---

## 🧩 Design Patterns

### 1. MVC (Model – View – Controller)

```
Controller  →  reçoit la requête HTTP, valide les entrées
Service     →  applique la logique métier, retourne des DTOs
Repository  →  accède à la base de données via JPA
```

```java
// CourseController.java (C)
@GetMapping("/{id}")
public ResponseEntity<CourseDTO.Response> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(courseService.findById(id)); // délègue au Service
}

// CourseService.java (M)
public CourseDTO.Response findById(UUID id) {
    return courseRepository.findByIdWithInstructor(id)
        .map(this::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException(...));
}
```

---

### 2. Strategy — Paiements

**Problème :** 3 méthodes de paiement avec des comportements très différents.  
**Solution :** encapsuler chaque algorithme dans une classe séparée.

```
PaymentStrategy (interface)
    ├── CreditCardPaymentStrategy  → simule Stripe
    ├── PayPalPaymentStrategy      → simule PayPal Orders API
    └── CryptoPaymentStrategy      → simule Coinbase Commerce

PaymentContext → sélectionne la bonne stratégie via Switch Expression
```

```java
// Le contexte délègue, le client ne connaît pas l'implémentation
PaymentStrategy.PaymentResult result = paymentContext.execute(
    Payment.PaymentMethod.PAYPAL,
    BigDecimal.valueOf(49.99),
    userId,
    "user@example.com"
);
```

**Avantage :** ajouter une 4ᵉ méthode (ex: Apple Pay) = créer une classe, sans toucher au reste.

---

### 3. Proxy — Cache transparent

**Problème :** le catalogue est consulté très fréquemment et coûteux en DB.  
**Solution :** un Proxy intercepte les appels, ajoute le cache, sans modifier le service.

```java
@Service
@Primary   // Spring injecte ce proxy partout où CourseService est demandé
public class CourseServiceProxy extends CourseService {

    private final Map<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();

    @Override
    public List<CourseDTO.Summary> findPublishedCourses() {
        return cached("courses:all", super::findPublishedCourses);
        //             ↑ clé cache   ↑ appel au vrai service si miss
    }

    @Override
    public CourseDTO.Response create(...) {
        evictAll();              // invalide le cache après mutation
        return super.create(...);
    }
}
```

**En production :** remplacer par `@Cacheable` Spring + Redis/Caffeine.

---

### 4. Factory — Abonnements

**Problème :** créer un `Subscription` requiert des calculs de dates et des règles métier.  
**Solution :** une Factory centralise la construction.

```java
@Component
public class SubscriptionFactory {

    public Subscription create(User user, Subscription.Plan plan, Payment payment) {
        LocalDateTime expiresAt = switch (plan) {  // Switch Expression Java 21
            case MONTHLY -> now.plusMonths(1);
            case YEARLY  -> now.plusYears(1);
            case TRIAL   -> now.plusDays(trialDays);
        };
        return Subscription.builder()
            .user(user).plan(plan).expiresAt(expiresAt)...build();
    }
}
```

---

### 5. Singleton — Spring Beans

Tous les `@Service`, `@Repository`, `@Component` Spring sont des **Singletons** par défaut.

```java
// Une seule instance partagée dans tout le contexte Spring
@Service
public class CourseService { ... }

// Équivalent Angular : providedIn: 'root'
@Injectable({ providedIn: 'root' })
export class CourseService { ... }
```

---

## 🗄 Base de données & Flyway

### Migrations automatiques au démarrage

```
backend/src/main/resources/db/migration/
├── V1__init_schema.sql   → Création des tables (users, courses, payments...)
└── V2__seed_data.sql     → Données de démonstration (8 cours, 3 instructeurs...)
```

Flyway exécute les migrations dans l'ordre au premier démarrage — **la DB est prête et peuplée automatiquement**.

### Modèle de données

```
users ──────────< enrollments >────────── courses
  │                                          │
  └── subscriptions                          └── categories
  │                                          │
  └── payments ──────────────────────────────┘
                                             │
                                           reviews
```

### Entités principales

| Table | Description |
|---|---|
| `users` | Étudiants, instructeurs, admins |
| `courses` | Catalogue avec niveau, prix, durée |
| `categories` | Dev Web, Data Science, DevOps... |
| `enrollments` | Inscriptions avec progression |
| `payments` | Historique (CB / PayPal / Crypto) |
| `subscriptions` | Abonnements Premium actifs |
| `reviews` | Avis et notes (1-5 étoiles) |

---

## 🐳 DevOps – Docker & Codespaces

### Docker Compose

```yaml
services:
  postgres:   # PostgreSQL 16 avec healthcheck
  backend:    # Spring Boot – attend que postgres soit healthy
  frontend:   # Angular buildé + Nginx avec proxy /api/*
  pgadmin:    # PGAdmin (profil "tools" optionnel)
```

**Démarrage ordonné avec healthchecks :**
```
postgres (healthy) → backend (healthy) → frontend
```

### Multi-stage Dockerfile Backend

```dockerfile
# Stage 1 : Build Maven
FROM eclipse-temurin:21-jdk-alpine AS builder
RUN mvn clean package -DskipTests

# Stage 2 : Runtime JRE uniquement (~200 Mo vs ~500 Mo avec JDK)
FROM eclipse-temurin:21-jre-alpine AS runtime
COPY --from=builder /build/target/*.jar app.jar
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseZGC"
```

### GitHub Codespaces

Le fichier `.devcontainer/devcontainer.json` configure :
- ✅ Java 21 + Maven
- ✅ Node 20 + npm
- ✅ Docker-in-Docker
- ✅ Extensions VS Code (Spring Boot Dashboard, Angular, Docker...)
- ✅ Forwarding automatique des ports 4200 / 8080 / 5432
- ✅ Script `setup.sh` : install dépendances + démarrage PostgreSQL

---

## 📖 API Reference

### Base URL : `http://localhost:8080/api/v1`

#### Cours

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/courses` | Tous les cours publiés |
| `GET` | `/courses/{id}` | Détail d'un cours |
| `GET` | `/courses/category/{id}` | Cours par catégorie |
| `GET` | `/courses/filter?level=BEGINNER&premium=false` | Filtrage avancé |
| `POST` | `/courses` | Créer un cours |
| `PATCH` | `/courses/{id}` | Modifier un cours |
| `DELETE` | `/courses/{id}` | Supprimer un cours |

#### Paiements

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/payments/course` | Acheter un cours |
| `POST` | `/payments/subscription` | Souscrire à un abonnement |

**Exemple – Achat par carte bancaire :**
```json
POST /api/v1/payments/course
{
  "userId": "b0000000-0000-0000-0000-000000000002",
  "courseId": "c0000000-0000-0000-0000-000000000001",
  "paymentMethod": "CREDIT_CARD"
}
```

**Exemple – Abonnement annuel PayPal :**
```json
POST /api/v1/payments/subscription
{
  "userId": "b0000000-0000-0000-0000-000000000002",
  "paymentMethod": "PAYPAL",
  "planType": "YEARLY"
}
```

### Swagger UI

Toute l'API est documentée et testable via :
```
http://localhost:8080/swagger-ui.html
```

---

## 📁 Structure du Projet

```
edumarket/
│
├── 📂 backend/                          # Spring Boot 3 / Java 21
│   ├── src/main/java/com/edumarket/
│   │   ├── EduMarketApplication.java    # Point d'entrée
│   │   ├── config/
│   │   │   └── SecurityConfig.java      # CORS, Sécurité, PasswordEncoder
│   │   ├── controller/
│   │   │   ├── CourseController.java    # MVC – Contrôleur REST
│   │   │   └── PaymentController.java
│   │   ├── service/
│   │   │   ├── CourseService.java       # Logique métier cours
│   │   │   └── PaymentService.java      # Orchestration paiements
│   │   ├── proxy/
│   │   │   └── CourseServiceProxy.java  # 🔷 Pattern Proxy (cache)
│   │   ├── payment/
│   │   │   ├── PaymentStrategy.java     # 🔷 Interface Strategy
│   │   │   ├── CreditCardPaymentStrategy.java
│   │   │   ├── PayPalPaymentStrategy.java
│   │   │   ├── CryptoPaymentStrategy.java
│   │   │   └── PaymentContext.java      # Contexte + Switch Expression
│   │   ├── factory/
│   │   │   └── SubscriptionFactory.java # 🔷 Pattern Factory
│   │   ├── model/                       # Entités JPA
│   │   ├── repository/                  # Spring Data JPA
│   │   ├── dto/                         # Records Java 21
│   │   └── exception/                   # GlobalExceptionHandler
│   │
│   ├── src/main/resources/
│   │   ├── application.yml              # Config (Virtual Threads, Flyway...)
│   │   └── db/migration/
│   │       ├── V1__init_schema.sql      # 🗄 Schéma complet
│   │       └── V2__seed_data.sql        # 🗄 8 cours + données démo
│   │
│   ├── src/test/java/com/edumarket/
│   │   ├── CourseServiceTest.java       # Tests + démo Virtual Threads
│   │   └── PaymentStrategyTest.java     # Tests Pattern Strategy
│   │
│   ├── Dockerfile                       # Multi-stage (JDK build → JRE runtime)
│   └── pom.xml                          # Java 21, Spring Boot 3.3, Flyway...
│
├── 📂 frontend/                         # Angular 17
│   ├── src/app/
│   │   ├── app.component.ts             # Root component + Navbar
│   │   ├── app.config.ts                # ApplicationConfig (sans NgModule)
│   │   ├── app.routes.ts                # Lazy loading routes
│   │   ├── core/
│   │   │   ├── models/models.ts         # Interfaces TypeScript
│   │   │   ├── services/
│   │   │   │   ├── course.service.ts    # Signals + Computed
│   │   │   │   ├── payment.service.ts   # Signals processing
│   │   │   │   └── auth.service.ts      # 🔷 Singleton + Signals
│   │   │   ├── guards/auth.guard.ts     # Guard fonctionnel
│   │   │   └── interceptors/            # HTTP intercepteur JWT
│   │   └── features/
│   │       ├── catalog/                 # Grille cours + filtres Signals
│   │       ├── course-detail/           # Détail cours @Input route param
│   │       ├── subscription/            # Plans + paiement abonnement
│   │       └── checkout/                # Achat à l'unité
│   │
│   ├── Dockerfile                       # Node build → Nginx serve
│   ├── nginx.conf                       # SPA fallback + proxy /api/*
│   └── angular.json
│
├── 📂 .devcontainer/
│   ├── devcontainer.json                # GitHub Codespaces config
│   └── setup.sh                         # Script d'init automatique
│
├── 📂 .github/workflows/
│   └── ci.yml                           # CI/CD GitHub Actions
│
├── docker-compose.yml                   # Stack complète orchestrée
└── README.md                            # Ce fichier
```

---

## 🧪 Tests

```bash
# Backend – tous les tests
cd backend && mvn test

# Backend – tests avec rapport de couverture
cd backend && mvn verify

# Frontend – tests unitaires
cd frontend && npm test

# Tests d'intégration (stack complète)
docker compose up -d
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/courses | jq 'length'
```

---

## 🔐 Sécurité

| Aspect | Implémentation |
|---|---|
| Authentification | HTTP Basic (démo) → JWT en production |
| Mots de passe | BCrypt (strength 12) |
| CORS | Configuré pour localhost:4200 |
| Headers | X-Frame-Options, X-Content-Type-Options |
| Validation | Bean Validation (`@NotBlank`, `@NotNull`...) |
| Erreurs | RFC 7807 Problem Details (pas de stacktrace exposé) |

---

## 📈 Évolutions Possibles

- [ ] **JWT** + Spring Security OAuth2 Resource Server
- [ ] **Redis** pour le cache distribué (remplacer le Proxy en mémoire)
- [ ] **Elasticsearch** pour la recherche full-text des cours
- [ ] **Kafka** pour les événements (paiement réussi → email)
- [ ] **GraalVM Native Image** pour un démarrage <100ms
- [ ] **Angular SSR** (Server-Side Rendering) pour le SEO
- [ ] **Stripe** / **PayPal SDK** réels en remplacement des simulations

---

## 📄 Licence

Distribué sous licence **MIT**. Voir [LICENSE](LICENSE) pour les détails.

---

<div align="center">
  Fait avec ☕ Java 21 + ❤️ Angular 17
</div>
