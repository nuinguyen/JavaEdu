package com.example.demo.controller;

import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.service.EnrollmentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MyCoursesController {

	private final EnrollmentService enrollmentService;

	public MyCoursesController(EnrollmentService enrollmentService) {
		this.enrollmentService = enrollmentService;
	}

	@GetMapping("/my-courses")
	public String myCourses(Model model, @AuthenticationPrincipal User currentUser) {
		if (currentUser == null) {
			return "redirect:/login";
		}
		List<Enrollment> enrollments = enrollmentService.findByUserId(currentUser.getId());
		model.addAttribute("title", "Khóa học của tôi");
		model.addAttribute("enrollments", enrollments);
		model.addAttribute("isAdmin", currentUser.getRole() != null && currentUser.getRole().contains("ADMIN"));
		return "my-courses";
	}
}
