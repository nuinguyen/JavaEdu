package com.example.demo.service;

import com.example.demo.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {

	List<Course> findAll();

	Page<Course> findAll(Pageable pageable);

	Page<Course> search(String keyword, Pageable pageable);

	Course findById(Long id);

	void softDelete(Long id);

	Course save(Course course);
}
