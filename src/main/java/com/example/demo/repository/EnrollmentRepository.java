package com.example.demo.repository;

import com.example.demo.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	@Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.course WHERE e.course.id = :courseId ORDER BY e.enrollDate DESC")
	List<Enrollment> findByCourseIdOrderByEnrollDateDesc(@Param("courseId") Long courseId);

	@Query(value = "SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.course ORDER BY e.enrollDate DESC",
		countQuery = "SELECT count(e) FROM Enrollment e")
	Page<Enrollment> findAllWithUserAndCourse(Pageable pageable);

	@Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.course ORDER BY e.enrollDate DESC")
	List<Enrollment> findAllWithUserAndCourse();

	long countByStatus(String status);

	@Query("SELECT e FROM Enrollment e JOIN FETCH e.user JOIN FETCH e.course WHERE e.user.id = :userId ORDER BY e.enrollDate DESC")
	List<Enrollment> findByUserIdOrderByEnrollDateDesc(@Param("userId") Long userId);

	int countByCourseIdAndStatus(Long courseId, String status);

	boolean existsByUserIdAndCourseId(Long userId, Long courseId);

	Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}
