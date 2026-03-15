package com.example.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
		model.addAttribute("title", "Không tìm thấy");
		model.addAttribute("message", ex.getMessage());
		model.addAttribute("statusCode", 404);
		return "error";
	}

	/** File upload vượt giới hạn: redirect để tránh forward request multipart sang /error (gây lỗi parse lần 2). */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, RedirectAttributes redirect) {
		log.warn("Upload vượt giới hạn: {}", ex.getMessage());
		redirect.addFlashAttribute("errorMessage", "Kích thước file vượt giới hạn (tối đa 5MB). Vui lòng chọn ảnh nhỏ hơn.");
		return "redirect:/admin/courses";
	}

	/** Thiếu tài nguyên tĩnh (favicon.ico, v.v.): trả 404 không body, không hiện trang lỗi. */
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
		log.debug("Tài nguyên không tồn tại: {}", ex.getResourcePath());
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleGeneric(Exception ex, Model model) {
		log.error("Lỗi hệ thống", ex);
		model.addAttribute("title", "Lỗi hệ thống");
		model.addAttribute("message", "Đã xảy ra lỗi. Vui lòng thử lại sau. Chi tiết xem Run/Debug Console.");
		model.addAttribute("statusCode", 500);
		return "error";
	}
}
