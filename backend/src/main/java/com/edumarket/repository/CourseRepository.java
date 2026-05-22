package com.edumarket.repository;

import com.edumarket.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    List<Course> findByPublishedTrueOrderByCreatedAtDesc();

    List<Course> findByCategoryIdAndPublishedTrue(Integer categoryId);

    Optional<Course> findBySlug(String slug);

    @Query("""
        SELECT c FROM Course c
        JOIN FETCH c.instructor
        LEFT JOIN FETCH c.category
        WHERE c.published = true
        AND (:level IS NULL OR c.level = :level)
        AND (:premium IS NULL OR c.premium = :premium)
        ORDER BY c.createdAt DESC
        """)
    List<Course> findFiltered(
        @Param("level")   Course.Level level,
        @Param("premium") Boolean premium
    );

    @Query("SELECT c FROM Course c JOIN FETCH c.instructor WHERE c.id = :id")
    Optional<Course> findByIdWithInstructor(@Param("id") UUID id);

    boolean existsBySlug(String slug);
}
