package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String q, Model model) {
		List<User> users = (q != null && !q.isBlank())
			? userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q.trim(), q.trim())
			: userRepository.findAll();
		model.addAttribute("title", "Danh sách User");
		model.addAttribute("users", users);
		model.addAttribute("searchKeyword", q != null ? q : "");
		return "users/users";
	}

	@GetMapping("/new")
	public String formNew(Model model) {
		model.addAttribute("title", "Thêm User");
		model.addAttribute("user", new User());
		return "users/user-form";
	}

	@GetMapping("/{id}/edit")
	public String formEdit(@PathVariable Long id, Model model, RedirectAttributes redirect) {
		return userRepository.findById(id)
			.map(user -> {
				user.setPassword(null);
				model.addAttribute("title", "Sửa User");
				model.addAttribute("user", user);
				return "users/user-form";
			})
			.orElseGet(() -> {
				redirect.addFlashAttribute("message", "Không tìm thấy user.");
				return "redirect:/users";
			});
	}

	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("user") User user,
					   BindingResult bindingResult,
					   Model model,
					   RedirectAttributes redirect) {
		boolean isNew = (user.getId() == null);
		if (isNew && (user.getPassword() == null || user.getPassword().isBlank())) {
			bindingResult.rejectValue("password", "NotBlank", "Mật khẩu không được để trống khi thêm user.");
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("title", isNew ? "Thêm User" : "Sửa User");
			return "users/user-form";
		}
		if (isNew || (user.getPassword() != null && !user.getPassword().isBlank())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		} else {
			userRepository.findById(user.getId()).ifPresent(existing ->
				user.setPassword(existing.getPassword()));
		}
		userRepository.save(user);
		redirect.addFlashAttribute("message", isNew ? "Đã thêm user." : "Đã cập nhật user.");
		return "redirect:/users";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirect) {
		userRepository.deleteById(id);
		redirect.addFlashAttribute("message", "Đã xóa user.");
		return "redirect:/users";
	}
}
