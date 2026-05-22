package com.edumarket.dto;

import com.edumarket.model.Course;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTOs définis comme Records Java 21 – immutables par nature.
 */
public final class CourseDTO {

    // ── Réponse ──────────────────────────────────────────────────────────
    public record Response(
        UUID            id,
        String          title,
        String          slug,
        String          description,
        String          instructorName,
        String          categoryName,
        BigDecimal      price,
        BigDecimal      durationHours,
        String          level,
        String          language,
        String          thumbnailUrl,
        boolean         premium,
        LocalDateTime   createdAt
    ) {}

    // ── Création ─────────────────────────────────────────────────────────
    public record CreateRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank                  String description,
        @NotNull                   UUID   instructorId,
        Integer                    categoryId,
        @NotNull @DecimalMin("0")  BigDecimal price,
        BigDecimal                 durationHours,
        Course.Level               level,
        String                     language,
        String                     thumbnailUrl,
        boolean                    premium
    ) {}

    // ── Mise à jour partielle ─────────────────────────────────────────────
    public record UpdateRequest(
        String        title,
        String        description,
        BigDecimal    price,
        BigDecimal    durationHours,
        Course.Level  level,
        boolean       published
    ) {}

    // ── Résumé pour le catalogue ──────────────────────────────────────────
    public record Summary(
        UUID        id,
        String      title,
        String      slug,
        String      instructorName,
        String      categoryName,
        BigDecimal  price,
        String      level,
        boolean     premium,
        String      thumbnailUrl
    ) {}
}
