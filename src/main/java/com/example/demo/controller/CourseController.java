package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

	private final CourseRepository courseRepository;

	public CourseController(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	@GetMapping
	public String list(Model model) {
		List<Course> courses = courseRepository.findAll();
		model.addAttribute("title", "Danh sách khóa học");
		model.addAttribute("courses", courses);
		return "courses/courses";
	}

	@GetMapping("/new")
	public String formNew(Model model) {
		model.addAttribute("title", "Thêm khóa học");
		model.addAttribute("course", new Course());
		return "courses/course-form";
	}

	@GetMapping("/{id}/edit")
	public String formEdit(@PathVariable Long id, Model model, RedirectAttributes redirect) {
		return courseRepository.findById(id)
			.map(course -> {
				model.addAttribute("title", "Sửa khóa học");
				model.addAttribute("course", course);
				return "courses/course-form";
			})
			.orElseGet(() -> {
				redirect.addFlashAttribute("message", "Không tìm thấy khóa học.");
				return "redirect:/courses";
			});
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("course") Course course,
					   BindingResult bindingResult,
					   Model model,
					   RedirectAttributes redirect) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("title", course.getId() == null ? "Thêm khóa học" : "Sửa khóa học");
			return "courses/course-form";
		}
		boolean isNew = (course.getId() == null);
		courseRepository.save(course);
		redirect.addFlashAttribute("message", isNew ? "Đã thêm khóa học." : "Đã cập nhật khóa học.");
		return "redirect:/courses";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirect) {
		courseRepository.deleteById(id);
		redirect.addFlashAttribute("message", "Đã xóa khóa học.");
		return "redirect:/courses";
	}
}

