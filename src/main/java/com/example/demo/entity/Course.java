package com.example.demo.entity;

import com.example.demo.validation.StartDateInFuture;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "courses")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Tiêu đề khóa học không được để trống")
	@Size(max = 150, message = "Tiêu đề không được dài quá 150 ký tự")
	@Column(nullable = false, length = 150)
	private String title;

	@Size(max = 1000, message = "Mô tả không được dài quá 1000 ký tự")
	@Column(length = 1000)
	private String description;

	@NotBlank(message = "Tên giảng viên không được để trống")
	@Size(max = 100, message = "Tên giảng viên không được dài quá 100 ký tự")
	@Column(nullable = false, length = 100)
	private String instructor;

	@Min(value = 1, message = "Sĩ số tối đa phải ít nhất là 1")
	@Max(value = 500, message = "Sĩ số tối đa không được lớn hơn 500")
	@Column(name = "max_students", nullable = false)
	private Integer maxStudents;

	@StartDateInFuture
	@Column(name = "start_date")
	private LocalDate startDate;

	/** Đường dẫn ảnh (URL hoặc path sau upload). */
	@Size(max = 500, message = "Đường dẫn ảnh không quá 500 ký tự")
	@Column(name = "image_url", length = 500)
	private String imageUrl;

	/** Thời lượng khóa học (số tuần). */
	@Min(value = 1, message = "Thời lượng ít nhất 1 tuần")
	@Max(value = 52, message = "Thời lượng không quá 52 tuần")
	@Column(name = "duration_weeks")
	private Integer durationWeeks;

	/** Học phí (null = miễn phí hoặc chưa công bố). */
	@DecimalMin(value = "0", message = "Học phí không được âm")
	@Column(precision = 12, scale = 2)
	private BigDecimal price;

	@Column(nullable = false)
	private boolean deleted = false;

	public Course() {
	}

	public Course(String title, String description, String instructor, Integer maxStudents) {
		this.title = title;
		this.description = description;
		this.instructor = instructor;
		this.maxStudents = maxStudents;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInstructor() {
		return instructor;
	}

	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}

	public Integer getMaxStudents() {
		return maxStudents;
	}

	public void setMaxStudents(Integer maxStudents) {
		this.maxStudents = maxStudents;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getDurationWeeks() {
		return durationWeeks;
	}

	public void setDurationWeeks(Integer durationWeeks) {
		this.durationWeeks = durationWeeks;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}

