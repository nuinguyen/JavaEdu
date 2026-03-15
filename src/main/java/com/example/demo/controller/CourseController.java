package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.service.CourseService;
import com.example.demo.service.EnrollmentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

	private final CourseService courseService;
	private final EnrollmentService enrollmentService;

	public CourseController(CourseService courseService, EnrollmentService enrollmentService) {
		this.courseService = courseService;
		this.enrollmentService = enrollmentService;
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model,
						 @AuthenticationPrincipal User currentUser) {
		Course course = courseService.findById(id);
		int approvedCount = enrollmentService.countApprovedByCourseId(id);
		List<com.example.demo.entity.Enrollment> courseEnrollments = enrollmentService.findByCourseId(id);
		boolean alreadyEnrolled = currentUser != null && courseEnrollments.stream()
			.anyMatch(e -> e.getUser().getId().equals(currentUser.getId()));
		boolean isFull = approvedCount >= course.getMaxStudents();
		boolean canEnroll = currentUser != null && !alreadyEnrolled && !isFull;
		model.addAttribute("title", course.getTitle());
		model.addAttribute("course", course);
		model.addAttribute("approvedCount", approvedCount);
		model.addAttribute("canEnroll", canEnroll);
		model.addAttribute("alreadyEnrolled", alreadyEnrolled);
		model.addAttribute("enrollmentStatus", alreadyEnrolled ? courseEnrollments.stream()
			.filter(e -> e.getUser().getId().equals(currentUser.getId())).findFirst().map(com.example.demo.entity.Enrollment::getStatus).orElse(null) : null);
		model.addAttribute("isFull", isFull);
		model.addAttribute("notLoggedIn", currentUser == null);
		model.addAttribute("enrollments", courseEnrollments);
		model.addAttribute("isAdmin", currentUser != null && currentUser.getRole() != null
			&& currentUser.getRole().contains("ADMIN"));
		return "courses/detail";
	}

	/** Trang đăng ký khóa học (form đơn giản, user viết HTML sau). */
	@GetMapping("/{id}/enroll")
	public String enrollPage(@PathVariable Long id, Model model,
						   @AuthenticationPrincipal User currentUser,
						   RedirectAttributes redirect) {
		if (currentUser == null) {
			redirect.addFlashAttribute("message", "Vui lòng đăng nhập để đăng ký khóa học.");
			return "redirect:/login";
		}
		Course course = courseService.findById(id);
		int approvedCount = enrollmentService.countApprovedByCourseId(id);
		List<com.example.demo.entity.Enrollment> courseEnrollments = enrollmentService.findByCourseId(id);
		boolean alreadyEnrolled = courseEnrollments.stream()
			.anyMatch(e -> e.getUser().getId().equals(currentUser.getId()));
		boolean isFull = approvedCount >= course.getMaxStudents();
		if (alreadyEnrolled || isFull) {
			redirect.addFlashAttribute("errorMessage", alreadyEnrolled ? "Bạn đã đăng ký khóa học này." : "Khóa học đã đủ sĩ số.");
			return "redirect:/courses/" + id;
		}
		model.addAttribute("title", "Đăng ký khóa học");
		model.addAttribute("course", course);
		model.addAttribute("isAdmin", currentUser.getRole() != null && currentUser.getRole().contains("ADMIN"));
		return "courses/enroll";
	}

	@PostMapping("/{id}/enroll")
	public String enrollSubmit(@PathVariable Long id, @AuthenticationPrincipal User currentUser,
							  RedirectAttributes redirect) {
		String error = enrollmentService.enroll(currentUser, id);
		if (error != null) {
			redirect.addFlashAttribute("errorMessage", error);
		} else {
			redirect.addFlashAttribute("message", "Đã gửi đăng ký. Chờ admin duyệt.");
		}
		return "redirect:/courses/" + id;
	}

}

