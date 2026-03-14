package com.example.demo.exception;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(String resource, Long id) {
		super(resource + " không tồn tại với id: " + id);
	}
}
