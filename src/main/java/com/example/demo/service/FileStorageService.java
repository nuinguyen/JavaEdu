package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service lưu file upload (ảnh khóa học, v.v.).
 * Trả về đường dẫn URL để lưu vào DB (ví dụ /upload/courses/xxx.jpg).
 */
public interface FileStorageService {

	/**
	 * Lưu file vào thư mục con (ví dụ "courses").
	 * @param file file upload
	 * @param subDir thư mục con (courses, avatars, ...)
	 * @return đường dẫn dạng /upload/subDir/filename để dùng làm src ảnh, hoặc null nếu lỗi
	 */
	String store(MultipartFile file, String subDir);
}
