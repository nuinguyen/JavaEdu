package com.example.demo.config;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtUtil jwtUtil;

	@Value("${app.jwt.cookie-name:token}")
	private String cookieName;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtUtil jwtUtil) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtUtil = jwtUtil;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.ignoringRequestMatchers("/login", "/logout", "/admin/courses/save", "/admin/courses/*/delete"))
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/login", "/logout", "/css/**", "/js/**", "/upload/**", "/error").permitAll()
				.requestMatchers("/admin/users/**", "/admin/enrollments/**", "/admin/courses/**").hasRole("ADMIN")
				.requestMatchers("/catalog", "/courses/**", "/my-courses", "/", "/about", "/admin").hasAnyRole("ADMIN", "USER")
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler((request, response, authentication) -> {
					String username = authentication.getName();
					String role = authentication.getAuthorities().stream()
						.findFirst()
						.map(a -> a.getAuthority())
						.orElse("ROLE_USER");
					String token = jwtUtil.generateToken(username, role);
					Cookie cookie = new Cookie(cookieName, token);
					cookie.setHttpOnly(true);
					cookie.setPath("/");
					cookie.setMaxAge(60 * 60 * 24);
					response.addCookie(cookie);
					response.sendRedirect("/");
				})
				.failureUrl("/login?error")
			);

		return http.build();
	}
}
