package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.service.CourseService;
import com.example.demo.service.EnrollmentService;
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

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

	private final CourseService courseService;
	private final EnrollmentService enrollmentService;
	private final FileStorageService fileStorageService;

	public CourseController(CourseService courseService, EnrollmentService enrollmentService,
							FileStorageService fileStorageService) {
		this.courseService = courseService;
		this.enrollmentService = enrollmentService;
		this.fileStorageService = fileStorageService;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String q,
					   @RequestParam(defaultValue = "0") int page,
					   @RequestParam(defaultValue = "10") int size,
					   Model model, @AuthenticationPrincipal User currentUser) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Course> coursePage = courseService.search(q != null ? q : "", pageable);
		model.addAttribute("title", "Danh sách khóa học");
		model.addAttribute("coursePage", coursePage);
		model.addAttribute("courses", coursePage.getContent());
		model.addAttribute("searchKeyword", q != null ? q : "");
		model.addAttribute("isAdmin", currentUser != null && currentUser.getRole() != null
			&& currentUser.getRole().contains("ADMIN"));
		model.addAttribute("currentUser", currentUser);
		return "courses/courses";
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
		return "redirect:/courses";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirect) {
		courseService.softDelete(id);
		redirect.addFlashAttribute("message", "Đã xóa khóa học.");
		return "redirect:/courses";
	}
}

