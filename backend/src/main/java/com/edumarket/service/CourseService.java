package com.edumarket.service;

import com.edumarket.dto.CourseDTO;
import com.edumarket.exception.ResourceNotFoundException;
import com.edumarket.model.Course;
import com.edumarket.repository.CourseRepository;
import com.edumarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │              DESIGN PATTERN : MVC – Service Layer           │
 * ├─────────────────────────────────────────────────────────────┤
 * │  CourseService est la couche métier (M de MVC).             │
 * │  Elle orchestre les entités JPA, applique les règles        │
 * │  business, et retourne des DTOs vers les contrôleurs.       │
 * │                                                             │
 * │  Note : CourseServiceProxy étend cette classe et ajoute     │
 * │  du caching transparent (Pattern Proxy).                    │
 * └─────────────────────────────────────────────────────────────┘
 *
 * Java 21 : Virtual Threads activés globalement via
 * spring.threads.virtual.enabled=true – chaque requête HTTP
 * s'exécute sur un Virtual Thread léger sans bloquer de threads
 * de plateforme (pas besoin de @Async pour le non-bloquant).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository   userRepository;

    // ── Lecture ───────────────────────────────────────────────────────────

    public List<CourseDTO.Summary> findPublishedCourses() {
        return courseRepository.findByPublishedTrueOrderByCreatedAtDesc()
            .stream()
            .map(this::toSummary)
            .toList();
    }

    public List<CourseDTO.Summary> findByCategory(Integer categoryId) {
        return courseRepository.findByCategoryIdAndPublishedTrue(categoryId)
            .stream()
            .map(this::toSummary)
            .toList();
    }

    public CourseDTO.Response findById(UUID id) {
        return courseRepository.findByIdWithInstructor(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable : " + id));
    }

    public List<CourseDTO.Summary> findFiltered(Course.Level level, Boolean premium) {
        return courseRepository.findFiltered(level, premium)
            .stream()
            .map(this::toSummary)
            .toList();
    }

    // ── Mutations ─────────────────────────────────────────────────────────

    @Transactional
    public CourseDTO.Response create(CourseDTO.CreateRequest request) {
        var instructor = userRepository.findById(request.instructorId())
            .orElseThrow(() -> new ResourceNotFoundException("Instructeur introuvable"));

        var slug = slugify(request.title());
        if (courseRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        var course = Course.builder()
            .title(request.title())
            .slug(slug)
            .description(request.description())
            .instructor(instructor)
            .price(request.price())
            .durationHours(request.durationHours())
            .level(request.level() != null ? request.level() : Course.Level.BEGINNER)
            .language(request.language() != null ? request.language() : "fr")
            .thumbnailUrl(request.thumbnailUrl())
            .premium(request.premium())
            .published(false)
            .build();

        var saved = courseRepository.save(course);
        log.info("Cours créé : {} ({})", saved.getTitle(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public CourseDTO.Response update(UUID id, CourseDTO.UpdateRequest request) {
        var course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable : " + id));

        if (request.title()         != null) course.setTitle(request.title());
        if (request.description()   != null) course.setDescription(request.description());
        if (request.price()         != null) course.setPrice(request.price());
        if (request.durationHours() != null) course.setDurationHours(request.durationHours());
        if (request.level()         != null) course.setLevel(request.level());
        course.setPublished(request.published());

        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public void delete(UUID id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cours introuvable : " + id);
        }
        courseRepository.deleteById(id);
        log.info("Cours supprimé : {}", id);
    }

    // ── Mappers ───────────────────────────────────────────────────────────

    protected CourseDTO.Summary toSummary(Course c) {
        return new CourseDTO.Summary(
            c.getId(),
            c.getTitle(),
            c.getSlug(),
            c.getInstructor() != null ? c.getInstructor().getFullName() : "—",
            c.getCategory()   != null ? c.getCategory().getName()      : "—",
            c.getPrice(),
            c.getLevel().name(),
            c.isPremium(),
            c.getThumbnailUrl()
        );
    }

    protected CourseDTO.Response toResponse(Course c) {
        return new CourseDTO.Response(
            c.getId(),
            c.getTitle(),
            c.getSlug(),
            c.getDescription(),
            c.getInstructor() != null ? c.getInstructor().getFullName() : "—",
            c.getCategory()   != null ? c.getCategory().getName()      : "—",
            c.getPrice(),
            c.getDurationHours(),
            c.getLevel().name(),
            c.getLanguage(),
            c.getThumbnailUrl(),
            c.isPremium(),
            c.getCreatedAt()
        );
    }

    // ── Utilitaires ───────────────────────────────────────────────────────

    private static String slugify(String text) {
        return text.toLowerCase()
            .replaceAll("[àáâãäå]", "a")
            .replaceAll("[èéêë]",   "e")
            .replaceAll("[îï]",     "i")
            .replaceAll("[ôö]",     "o")
            .replaceAll("[ùûü]",    "u")
            .replaceAll("[ç]",      "c")
            .replaceAll("[^a-z0-9\\s-]", "")
            .trim()
            .replaceAll("\\s+", "-");
    }
}
