package com.edumarket.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA représentant un cours EduMarket.
 *
 * Utilise les Records-style immutabilité via @Value pour les DTO
 * mais garde un @Entity mutable pour la couche JPA.
 */
@Entity
@Table(name = "courses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"instructor", "category"})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_hours", precision = 5, scale = 1)
    private BigDecimal durationHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Level level;

    @Column(nullable = false, length = 10)
    private String language = "fr";

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "is_published", nullable = false)
    private boolean published = false;

    @Column(name = "is_premium", nullable = false)
    private boolean premium = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Level {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
