package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/catalog")
public class CourseCatalogController {

	private final CourseService courseService;

	public CourseCatalogController(CourseService courseService) {
		this.courseService = courseService;
	}

	/**
	 * Trang đăng ký khóa học dành cho user – giao diện catalog (card grid).
	 */
	@GetMapping
	public String catalog(@RequestParam(required = false) String q,
						  @RequestParam(defaultValue = "0") int page,
						  @RequestParam(defaultValue = "12") int size,
						  Model model,
						  @AuthenticationPrincipal User currentUser) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Course> coursePage = courseService.search(q != null ? q : "", pageable);
		model.addAttribute("title", "Đăng ký khóa học");
		model.addAttribute("coursePage", coursePage);
		model.addAttribute("courses", coursePage.getContent());
		model.addAttribute("searchKeyword", q != null ? q : "");
		model.addAttribute("isAdmin", currentUser != null && currentUser.getRole() != null
			&& currentUser.getRole().contains("ADMIN"));
		return "courses/catalog";
	}
}
