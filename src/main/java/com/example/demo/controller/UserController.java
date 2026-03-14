package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String q,
					   @RequestParam(defaultValue = "0") int page,
					   @RequestParam(defaultValue = "10") int size,
					   Model model, @AuthenticationPrincipal User currentUser) {
		Pageable pageable = PageRequest.of(page, size);
		Page<User> userPage = (q != null && !q.isBlank())
			? userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q.trim(), q.trim(), pageable)
			: userRepository.findAll(pageable);
		model.addAttribute("title", "Danh sách User");
		model.addAttribute("userPage", userPage);
		model.addAttribute("users", userPage.getContent());
		model.addAttribute("searchKeyword", q != null ? q : "");
		model.addAttribute("isAdmin", true);
		model.addAttribute("currentUser", currentUser);
		return "users/users";
	}

	@GetMapping("/new")
	public String formNew(Model model, @AuthenticationPrincipal User currentUser) {
		model.addAttribute("title", "Thêm User");
		model.addAttribute("user", new User());
		model.addAttribute("isAdmin", true);
		model.addAttribute("currentUser", currentUser);
		return "users/user-form";
	}

	@GetMapping("/{id}/edit")
	public String formEdit(@PathVariable Long id, Model model, RedirectAttributes redirect, @AuthenticationPrincipal User currentUser) {
		return userRepository.findById(id)
			.map(user -> {
				user.setPassword(null);
				model.addAttribute("title", "Sửa User");
				model.addAttribute("user", user);
				model.addAttribute("isAdmin", true);
				model.addAttribute("currentUser", currentUser);
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
					   RedirectAttributes redirect,
					   @AuthenticationPrincipal User currentUser) {
		boolean isNew = (user.getId() == null);
		if (isNew && (user.getPassword() == null || user.getPassword().isBlank())) {
			bindingResult.rejectValue("password", "NotBlank", "Mật khẩu không được để trống khi thêm user.");
		}
		// Chuẩn hóa và kiểm tra trùng tên đăng nhập
		if (user.getUsername() != null) {
			user.setUsername(user.getUsername().trim());
		}
		if (user.getUsername() != null && !user.getUsername().isBlank()) {
			boolean usernameExists = userRepository.existsByUsername(user.getUsername());
			if (isNew && usernameExists) {
				bindingResult.rejectValue("username", "Duplicate", "Tên đăng nhập này đã được sử dụng.");
			} else if (!isNew && usernameExists) {
				userRepository.findById(user.getId()).ifPresent(existing -> {
					if (!existing.getUsername().equals(user.getUsername())) {
						bindingResult.rejectValue("username", "Duplicate", "Tên đăng nhập này đã được sử dụng.");
					}
				});
			}
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("title", isNew ? "Thêm User" : "Sửa User");
			model.addAttribute("isAdmin", true);
			model.addAttribute("currentUser", currentUser);
			return "users/user-form";
		}
		if (isNew || (user.getPassword() != null && !user.getPassword().isBlank())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		} else {
			userRepository.findById(user.getId()).ifPresent(existing ->
				user.setPassword(existing.getPassword()));
		}
		try {
			userRepository.save(user);
			redirect.addFlashAttribute("message", isNew ? "Đã thêm user." : "Đã cập nhật user.");
			return "redirect:/users";
		} catch (Exception e) {
			log.error("Lỗi khi lưu user: {}", e.getMessage(), e);
			model.addAttribute("title", isNew ? "Thêm User" : "Sửa User");
			model.addAttribute("isAdmin", true);
			model.addAttribute("currentUser", currentUser);
			model.addAttribute("saveError", "Không lưu được. Chi tiết: " + e.getMessage() + ". Xem log trong Run/Debug Console.");
			return "users/user-form";
		}
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirect) {
		userRepository.deleteById(id);
		redirect.addFlashAttribute("message", "Đã xóa user.");
		return "redirect:/users";
	}
}
