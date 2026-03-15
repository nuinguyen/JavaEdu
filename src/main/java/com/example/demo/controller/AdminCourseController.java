package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.service.CourseService;
import com.example.demo.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/courses")
public class AdminCourseController {

	private final CourseService courseService;
	private final FileStorageService fileStorageService;

	public AdminCourseController(CourseService courseService, FileStorageService fileStorageService) {
		this.courseService = courseService;
		this.fileStorageService = fileStorageService;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String q,
					   @RequestParam(defaultValue = "0") int page,
					   @RequestParam(defaultValue = "10") int size,
					   Model model, @AuthenticationPrincipal User currentUser) {
		boolean isAdmin = currentUser != null && currentUser.getRole() != null
				&& currentUser.getRole().contains("ADMIN");
		if (!isAdmin) {
			return "redirect:/catalog";
		}
		Pageable pageable = PageRequest.of(page, size);
		Page<Course> coursePage = courseService.search(q != null ? q : "", pageable);
		model.addAttribute("title", "Quản lý khóa học");
		model.addAttribute("coursePage", coursePage);
		model.addAttribute("courses", coursePage.getContent());
		model.addAttribute("searchKeyword", q != null ? q : "");
		model.addAttribute("isAdmin", true);
		model.addAttribute("currentUser", currentUser);
		return "courses/courses";
	}

	@GetMapping("/new")
	public String formNew(Model model, @AuthenticationPrincipal User currentUser) {
		model.addAttribute("title", "Thêm khóa học");
		model.addAttribute("course", new Course());
		model.addAttribute("isAdmin", currentUser != null && currentUser.getRole() != null && currentUser.getRole().contains("ADMIN"));
		model.addAttribute("currentUser", currentUser);
		return "courses/course-form";
	}

	@GetMapping("/{id}/edit")
	public String formEdit(@PathVariable Long id, Model model, @AuthenticationPrincipal User currentUser) {
		Course course = courseService.findById(id);
		model.addAttribute("title", "Sửa khóa học");
		model.addAttribute("course", course);
		model.addAttribute("isAdmin", currentUser != null && currentUser.getRole() != null && currentUser.getRole().contains("ADMIN"));
		model.addAttribute("currentUser", currentUser);
		return "courses/course-form";
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("course") Course course,
					   BindingResult bindingResult,
					   @RequestParam(required = false) MultipartFile imageFile,
					   Model model,
					   RedirectAttributes redirect,
					   @AuthenticationPrincipal User currentUser) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("title", course.getId() == null ? "Thêm khóa học" : "Sửa khóa học");
			model.addAttribute("isAdmin", currentUser != null && currentUser.getRole() != null && currentUser.getRole().contains("ADMIN"));
			model.addAttribute("currentUser", currentUser);
			return "courses/course-form";
		}
		if (imageFile != null && !imageFile.isEmpty()) {
			String savedPath = fileStorageService.store(imageFile, "courses");
			if (savedPath != null) {
				course.setImageUrl(savedPath);
			}
		}
		boolean isNew = (course.getId() == null);
		courseService.save(course);
		redirect.addFlashAttribute("message", isNew ? "Đã thêm khóa học." : "Đã cập nhật khóa học.");
		return "redirect:/admin/courses";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirect) {
		courseService.softDelete(id);
		redirect.addFlashAttribute("message", "Đã xóa khóa học.");
		return "redirect:/admin/courses";
	}
}
