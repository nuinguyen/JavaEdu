package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

	@Value("${app.jwt.secret}")
	private String secret;

	@Value("${app.jwt.expiration-ms:86400000}")
	private long expirationMs;

	private SecretKey signingKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String username, String role) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expirationMs);
		return Jwts.builder()
			.subject(username)
			.claim("role", role != null ? role : "ROLE_USER")
			.issuedAt(now)
			.expiration(expiry)
			.signWith(signingKey())
			.compact();
	}

	public String getUsernameFromToken(String token) {
		Claims payload = Jwts.parser()
			.verifyWith(signingKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
		return payload.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
