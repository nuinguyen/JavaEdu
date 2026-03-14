package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "course_id"})
})
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(name = "enroll_date", nullable = false)
	private LocalDateTime enrollDate;

	@Column(nullable = false, length = 20)
	private String status = "PENDING";

	public static final String PENDING = "PENDING";
	public static final String APPROVED = "APPROVED";

	public Enrollment() {
	}

	public Enrollment(User user, Course course) {
		this.user = user;
		this.course = course;
		this.enrollDate = LocalDateTime.now();
		this.status = PENDING;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public LocalDateTime getEnrollDate() {
		return enrollDate;
	}

	public void setEnrollDate(LocalDateTime enrollDate) {
		this.enrollDate = enrollDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
