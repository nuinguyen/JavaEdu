package com.example.demo.service.impl;

import com.example.demo.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

	private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);

	@Value("${app.upload.dir:${user.dir}/upload}")
	private String uploadDir;

	@Override
	public String store(MultipartFile file, String subDir) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		String originalFilename = StringUtils.getFilenameExtension(file.getOriginalFilename());
		String ext = originalFilename != null && !originalFilename.isBlank() ? "." + originalFilename : "";
		String filename = UUID.randomUUID().toString() + ext;
		Path root = Paths.get(uploadDir).resolve(subDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(root);
			Path target = root.resolve(filename);
			Files.copy(file.getInputStream(), target);
			// Trả về URL path để browser request: /upload/subDir/filename
			return "/upload/" + subDir + "/" + filename;
		} catch (IOException e) {
			log.error("Lỗi lưu file: {}", e.getMessage());
			return null;
		}
	}
}
