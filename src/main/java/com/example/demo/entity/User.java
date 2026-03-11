package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Entity // Hiểu là 1 thực thể, sẽ map với bảng trong DB
@Table(name = "tbl_staffs")
public class User implements UserDetails { // UserDetails là 1 interface trong Spring Security

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)//tự động tắng id
	private Long id;

	@NotBlank(message = "Họ tên không được để trống")
	@Size(max = 100, message = "Họ tên không được dài quá 100 ký tự")
	@Column(nullable = false, length = 100)
	private String name;

	@Email(message = "Email không hợp lệ")
	@Size(max = 150, message = "Email không được dài quá 150 ký tự")
	@Column(length = 150)
	private String email;

	@NotBlank(message = "Tên đăng nhập không được để trống")
	@Size(min = 3, max = 50, message = "Tên đăng nhập từ 3–50 ký tự")
	@Column(unique = true, length = 50)
	private String username;

	@Column(length = 255)
	private String password;

	@Column(length = 20)
	private String role = "ROLE_USER";

	@Column(nullable = false)
	private boolean active = true;

	public User() {
	}

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Stream.of(role)
			.filter(r -> r != null && !r.isBlank())
			.map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
			.map(SimpleGrantedAuthority::new)
			.toList();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role != null && !role.startsWith("ROLE_") ? "ROLE_" + role : role;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
