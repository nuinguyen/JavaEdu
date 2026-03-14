package com.example.demo.service.impl;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.service.CourseService;
import com.example.demo.service.EnrollmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

	private static final Pattern EMAIL_PATTERN = Pattern.compile(
		"^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	private final EnrollmentRepository enrollmentRepository;
	private final CourseService courseService;

	public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
								 CourseService courseService) {
		this.enrollmentRepository = enrollmentRepository;
		this.courseService = courseService;
	}

	@Override
	@Transactional
	public String enroll(User user, Long courseId) {
		if (user == null) {
			return "Bạn cần đăng nhập để đăng ký khóa học.";
		}
		Course course;
		try {
			course = courseService.findById(courseId);
		} catch (ResourceNotFoundException e) {
			return "Khóa học không tồn tại.";
		}
		if (user.getEmail() == null || user.getEmail().isBlank()) {
			return "Học viên cần có email hợp lệ để đăng ký. Vui lòng cập nhật email trong tài khoản.";
		}
		if (!EMAIL_PATTERN.matcher(user.getEmail().trim()).matches()) {
			return "Email không đúng định dạng. Vui lòng cập nhật email trong tài khoản.";
		}
		if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
			return "Bạn đã đăng ký khóa học này rồi.";
		}
		int approved = enrollmentRepository.countByCourseIdAndStatus(courseId, Enrollment.APPROVED);
		if (approved >= course.getMaxStudents()) {
			return "Lớp đã đủ sĩ số. Không thể đăng ký thêm.";
		}
		Enrollment enrollment = new Enrollment(user, course);
		enrollmentRepository.save(enrollment);
		return null;
	}

	@Override
	public List<Enrollment> findByCourseId(Long courseId) {
		return enrollmentRepository.findByCourseIdOrderByEnrollDateDesc(courseId);
	}

	@Override
	public List<Enrollment> findByUserId(Long userId) {
		return enrollmentRepository.findByUserIdOrderByEnrollDateDesc(userId);
	}

	@Override
	public List<Enrollment> findAll() {
		return enrollmentRepository.findAllWithUserAndCourse();
	}

	@Override
	public Page<Enrollment> findAll(Pageable pageable) {
		return enrollmentRepository.findAllWithUserAndCourse(pageable);
	}

	@Override
	public long countByStatus(String status) {
		return enrollmentRepository.countByStatus(status);
	}

	@Override
	public int countApprovedByCourseId(Long courseId) {
		return enrollmentRepository.countByCourseIdAndStatus(courseId, Enrollment.APPROVED);
	}

	@Override
	@Transactional
	public void approve(Long enrollmentId) {
		enrollmentRepository.findById(enrollmentId).ifPresent(e -> {
			e.setStatus(Enrollment.APPROVED);
			enrollmentRepository.save(e);
		});
	}

	@Override
	@Transactional
	public void reject(Long enrollmentId) {
		enrollmentRepository.deleteById(enrollmentId);
	}

	@Override
	public Optional<Enrollment> findById(Long id) {
		return enrollmentRepository.findById(id);
	}
}
