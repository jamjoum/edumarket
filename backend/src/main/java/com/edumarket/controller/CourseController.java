package com.edumarket.controller;

import com.edumarket.dto.CourseDTO;
import com.edumarket.model.Course;
import com.edumarket.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * ┌─────────────────────────────────────────────────────────────┐
 * │              DESIGN PATTERN : MVC – Controller              │
 * ├─────────────────────────────────────────────────────────────┤
 * │  Le Controller (C de MVC) reçoit les requêtes HTTP,         │
 * │  valide les entrées, délègue au Service, et retourne        │
 * │  la réponse HTTP appropriée. Aucune logique métier ici.     │
 * └─────────────────────────────────────────────────────────────┘
 *
 * Note : CourseService est injecté mais Spring résout en réalité
 * CourseServiceProxy (Pattern Proxy, @Primary) de façon transparente.
 */
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Catalogue de cours EduMarket")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Lister tous les cours publiés")
    public ResponseEntity<List<CourseDTO.Summary>> getAll() {
        return ResponseEntity.ok(courseService.findPublishedCourses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un cours par ID")
    public ResponseEntity<CourseDTO.Response> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Cours par catégorie")
    public ResponseEntity<List<CourseDTO.Summary>> getByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(courseService.findByCategory(categoryId));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filtrer les cours (niveau, premium)")
    public ResponseEntity<List<CourseDTO.Summary>> filter(
        @RequestParam(required = false) Course.Level level,
        @RequestParam(required = false) Boolean      premium
    ) {
        return ResponseEntity.ok(courseService.findFiltered(level, premium));
    }

    @PostMapping
    @Operation(summary = "Créer un cours (instructeur/admin)")
    public ResponseEntity<CourseDTO.Response> create(@Valid @RequestBody CourseDTO.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(courseService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre à jour un cours")
    public ResponseEntity<CourseDTO.Response> update(
        @PathVariable UUID id,
        @RequestBody  CourseDTO.UpdateRequest request
    ) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un cours")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
