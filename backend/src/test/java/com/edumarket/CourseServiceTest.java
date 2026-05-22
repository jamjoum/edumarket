package com.edumarket.service;

import com.edumarket.model.Course;
import com.edumarket.repository.CourseRepository;
import com.edumarket.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires du CourseService.
 *
 * Inclut une démonstration des Virtual Threads Java 21.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService – Tests unitaires")
class CourseServiceTest {

    @Mock
    CourseRepository courseRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CourseService courseService;

    // ── Test CRUD ────────────────────────────────────────────────────────

    @Test
    @DisplayName("findPublishedCourses() retourne uniquement les cours publiés")
    void findPublishedCourses_returnsOnlyPublished() {
        // Arrange
        var mockCourse = buildCourse("Angular 17", true);
        when(courseRepository.findByPublishedTrueOrderByCreatedAtDesc())
            .thenReturn(List.of(mockCourse));

        // Act
        var result = courseService.findPublishedCourses();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Angular 17");
        verify(courseRepository).findByPublishedTrueOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("findById() lance une exception si cours introuvable")
    void findById_throwsWhenNotFound() {
        var id = UUID.randomUUID();
        when(courseRepository.findByIdWithInstructor(id)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
            com.edumarket.exception.ResourceNotFoundException.class,
            () -> courseService.findById(id)
        );
    }

    // ── Démonstration Virtual Threads Java 21 ────────────────────────────

    @Test
    @DisplayName("Virtual Threads – 10 000 threads légers sans saturation")
    void virtualThreads_massiveConcurrency() throws Exception {
        // Java 21 : Executors.newVirtualThreadPerTaskExecutor()
        // Crée un nouveau Virtual Thread par tâche – TRÈS léger (quelques ko de mémoire)
        var counter = new AtomicInteger(0);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new java.util.ArrayList<>();

            // Soumettre 10 000 tâches simultanées
            for (int i = 0; i < 10_000; i++) {
                futures.add(executor.submit(() -> {
                    // Simule un travail I/O (ex: requête DB)
                    Thread.sleep(1);  // Virtual Thread : bloque le VT, pas le thread OS
                    counter.incrementAndGet();
                    return null;
                }));
            }

            // Attendre toutes les tâches
            for (var future : futures) {
                future.get();
            }
        }

        // Tous les 10 000 Virtual Threads ont terminé
        assertThat(counter.get()).isEqualTo(10_000);
        // Avec des Platform Threads, cela nécessiterait un pool de 10 000 threads OS
        // (~10 Go de mémoire) – avec Virtual Threads : ~100 Mo
    }

    @Test
    @DisplayName("Switch Expression Java 21 – résolution de niveau")
    void switchExpression_levelResolution() {
        // Démonstration du Switch Expression Java 21
        for (var level : Course.Level.values()) {
            // Switch Expression : retourne une valeur, exhaustif, pas de fall-through
            String label = switch (level) {
                case BEGINNER     -> "Débutant";
                case INTERMEDIATE -> "Intermédiaire";
                case ADVANCED     -> "Avancé";
            };
            assertThat(label).isNotBlank();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private Course buildCourse(String title, boolean published) {
        var instructor = com.edumarket.model.User.builder()
            .id(UUID.randomUUID())
            .fullName("Test Instructor")
            .email("test@test.com")
            .role(com.edumarket.model.User.Role.INSTRUCTOR)
            .build();

        return Course.builder()
            .id(UUID.randomUUID())
            .title(title)
            .slug(title.toLowerCase().replace(" ", "-"))
            .price(BigDecimal.valueOf(49.99))
            .level(Course.Level.INTERMEDIATE)
            .language("fr")
            .instructor(instructor)
            .published(published)
            .premium(false)
            .build();
    }
}
