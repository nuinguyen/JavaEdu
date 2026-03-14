package com.example.demo.service;

import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

	/**
	 * Đăng ký khóa học. Kiểm tra: khóa tồn tại, chưa đầy, user có email hợp lệ, chưa đăng ký trùng.
	 * @return thông báo lỗi nếu không hợp lệ, null nếu thành công
	 */
	String enroll(User user, Long courseId);

	List<Enrollment> findByCourseId(Long courseId);

	List<Enrollment> findByUserId(Long userId);

	List<Enrollment> findAll();

	Page<Enrollment> findAll(Pageable pageable);

	long countByStatus(String status);

	int countApprovedByCourseId(Long courseId);

	void approve(Long enrollmentId);

	void reject(Long enrollmentId);

	Optional<Enrollment> findById(Long id);
}
