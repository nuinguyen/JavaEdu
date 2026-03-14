package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final EnrollmentRepository enrollmentRepository;

	public HomeController(UserRepository userRepository, CourseRepository courseRepository,
						  EnrollmentRepository enrollmentRepository) {
		this.userRepository = userRepository;
		this.courseRepository = courseRepository;
		this.enrollmentRepository = enrollmentRepository;
	}

	@GetMapping("/")
	public String index(Model model, @AuthenticationPrincipal User user) {
		model.addAttribute("title", "Trang chủ");
		model.addAttribute("isAdmin", user != null && user.getRole() != null && user.getRole().contains("ADMIN"));
		model.addAttribute("currentUser", user);
		return "index";
	}

	@GetMapping("/admin")
	public String adminDashboard(Model model, @AuthenticationPrincipal User user) {
		if (user == null || user.getRole() == null || !user.getRole().contains("ADMIN")) {
			return "redirect:/login";
		}
		model.addAttribute("title", "Dashboard");
		model.addAttribute("isAdmin", true);
		model.addAttribute("currentUser", user);
		model.addAttribute("totalUsers", userRepository.count());
		model.addAttribute("totalCourses", courseRepository.countByDeletedFalse());
		model.addAttribute("pendingEnrollments", enrollmentRepository.countByStatus("PENDING"));
		model.addAttribute("approvedEnrollments", enrollmentRepository.countByStatus("APPROVED"));
		return "admin-dashboard";
	}

	@GetMapping("/about")
	public String about(Model model, @AuthenticationPrincipal User user) {
		model.addAttribute("title", "Giới thiệu");
		model.addAttribute("isAdmin", user != null && user.getRole() != null && user.getRole().contains("ADMIN"));
		return "about";
	}
}
