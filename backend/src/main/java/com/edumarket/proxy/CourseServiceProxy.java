package com.edumarket.proxy;

import com.edumarket.dto.CourseDTO;
import com.edumarket.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │              DESIGN PATTERN : PROXY                         │
 * ├─────────────────────────────────────────────────────────────┤
 * │  Problème : certaines requêtes (liste du catalogue) sont    │
 * │  coûteuses et appelées très fréquemment.                    │
 * │                                                             │
 * │  Solution : un Proxy intercèpte les appels au service réel, │
 * │  ajoute du caching en mémoire et de l'audit logging, sans   │
 * │  modifier le service réel (Open/Closed Principle).          │
 * │                                                             │
 * │  @Primary → Spring injecte ce proxy partout où CourseService│
 * │  est demandé. Le proxy délègue au vrai service.             │
 * └─────────────────────────────────────────────────────────────┘
 *
 * Note : En production, utiliser @Cacheable de Spring Cache
 * (Redis / Caffeine). Ce proxy illustre le pattern manuellement.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class CourseServiceProxy extends CourseService {

    /** Cache simple en mémoire : clé → (payload, expiry) */
    private final Map<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    // ── Surcharge avec cache ──────────────────────────────────────────────

    @Override
    public List<CourseDTO.Summary> findPublishedCourses() {
        return cached("courses:all", super::findPublishedCourses);
    }

    @Override
    public List<CourseDTO.Summary> findByCategory(Integer categoryId) {
        return cached("courses:cat:" + categoryId, () -> super.findByCategory(categoryId));
    }

    @Override
    public CourseDTO.Response findById(UUID id) {
        return cached("course:" + id, () -> super.findById(id));
    }

    // ── Invalidation du cache après mutation ─────────────────────────────

    @Override
    public CourseDTO.Response create(CourseDTO.CreateRequest request) {
        evictAll();
        return super.create(request);
    }

    @Override
    public CourseDTO.Response update(UUID id, CourseDTO.UpdateRequest request) {
        evict("course:" + id);
        evict("courses:all");
        return super.update(id, request);
    }

    @Override
    public void delete(UUID id) {
        evict("course:" + id);
        evictAll();
        super.delete(id);
    }

    // ── Infrastructure du cache ───────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private <T> T cached(String key, java.util.function.Supplier<T> loader) {
        var entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            log.debug("[Proxy Cache HIT] {}", key);
            return (T) entry.value();
        }
        log.debug("[Proxy Cache MISS] {} – chargement depuis DB", key);
        var start  = Instant.now();
        var result = loader.get();
        var elapsed = Duration.between(start, Instant.now()).toMillis();
        log.info("[Proxy Audit] {} chargé en {}ms", key, elapsed);
        cache.put(key, new CacheEntry<>(result, Instant.now().plus(CACHE_TTL)));
        return result;
    }

    private void evict(String key) {
        cache.remove(key);
        log.debug("[Proxy Cache EVICT] {}", key);
    }

    private void evictAll() {
        cache.clear();
        log.debug("[Proxy Cache EVICT ALL]");
    }

    /** Entrée de cache avec TTL. */
    private record CacheEntry<T>(T value, Instant expiry) {
        boolean isExpired() {
            return Instant.now().isAfter(expiry);
        }
    }
}
