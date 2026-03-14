package com.example.demo.service.impl;

import com.example.demo.entity.Course;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CourseRepository;
import com.example.demo.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

	private final CourseRepository courseRepository;

	public CourseServiceImpl(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	@Override
	public List<Course> findAll() {
		return courseRepository.findByDeletedFalse();
	}

	@Override
	public Page<Course> findAll(Pageable pageable) {
		return courseRepository.findByDeletedFalse(pageable);
	}

	@Override
	public Page<Course> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return courseRepository.findByDeletedFalse(pageable);
		}
		return courseRepository.search(keyword.trim(), pageable);
	}

	@Override
	public Course findById(Long id) {
		return courseRepository.findByIdAndDeletedFalse(id)
			.orElseThrow(() -> new ResourceNotFoundException("Khóa học", id));
	}

	@Override
	@Transactional
	public Course save(Course course) {
		return courseRepository.save(course);
	}

	@Override
	@Transactional
	public void softDelete(Long id) {
		Course course = findById(id);
		course.setDeleted(true);
		courseRepository.save(course);
	}
}
