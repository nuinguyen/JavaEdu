package com.example.demo.repository;

import com.example.demo.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

	List<Course> findByDeletedFalse();

	Page<Course> findByDeletedFalse(Pageable pageable);

	@Query("SELECT c FROM Course c WHERE c.deleted = false AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(c.instructor) LIKE LOWER(CONCAT('%', :q, '%')))")
	Page<Course> search(@Param("q") String q, Pageable pageable);

	long countByDeletedFalse();

	Optional<Course> findByIdAndDeletedFalse(Long id);
}

