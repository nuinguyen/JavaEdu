package com.example.demo.controller;

import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.service.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/enrollments")
public class EnrollmentController {

	private final EnrollmentService enrollmentService;

	public EnrollmentController(EnrollmentService enrollmentService) {
		this.enrollmentService = enrollmentService;
	}

	@GetMapping
	public String list(@RequestParam(defaultValue = "0") int page,
					   @RequestParam(defaultValue = "10") int size,
					   Model model,
					   @AuthenticationPrincipal User currentUser) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Enrollment> enrollmentPage = enrollmentService.findAll(pageable);
		model.addAttribute("title", "Quản lý đăng ký");
		model.addAttribute("enrollmentPage", enrollmentPage);
		model.addAttribute("enrollments", enrollmentPage.getContent());
		model.addAttribute("currentUser", currentUser);
		return "enrollments/list";
	}

	@PostMapping("/{id}/approve")
	public String approve(@PathVariable Long id, @RequestParam(required = false) Long courseId,
						 RedirectAttributes redirect) {
		enrollmentService.approve(id);
		redirect.addFlashAttribute("message", "Đã duyệt đăng ký.");
		if (courseId != null) {
			return "redirect:/courses/" + courseId;
		}
		return "redirect:/admin/enrollments";
	}

	@PostMapping("/{id}/reject")
	public String reject(@PathVariable Long id, @RequestParam(required = false) Long courseId,
						RedirectAttributes redirect) {
		enrollmentService.reject(id);
		redirect.addFlashAttribute("message", "Đã từ chối đăng ký.");
		if (courseId != null) {
			return "redirect:/courses/" + courseId;
		}
		return "redirect:/admin/enrollments";
	}
}
