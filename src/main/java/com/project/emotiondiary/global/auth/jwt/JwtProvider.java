package com.project.emotiondiary.global.auth.jwt;

import static com.project.emotiondiary.global.error.type.AuthErrorCode.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.emotiondiary.domain.member.entity.Role;
import com.project.emotiondiary.global.error.exception.AuthException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

	private static final String TYPE_CLAIM_KEY = "type";
	private static final String EMAIL_CLAIM_KEY = "email";
	private static final String ROLE_CLAIM_KEY = "role";
	@Value("${spring.jwt.secret}")
	private String secretKey;

	@Value("${spring.jwt.valid.accessToken}")
	private Long accessTokenValid;

	@Value("${spring.jwt.valid.refreshToken}")
	private Long refreshTokenValid;

	private Key key;

	@PostConstruct
	protected void init() {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateAccessToken(String email, Role role) {
		Date now = new Date();
		Map<String, Object> claims = createClaims(email, "access");
		claims.put(ROLE_CLAIM_KEY, role);

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + accessTokenValid))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String generateRefreshToken(String email) {
		Date now = new Date();
		Map<String, Object> claims = createClaims(email, "refresh");

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + refreshTokenValid))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	// token 검증 메서드
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
			return true;
		} catch (ExpiredJwtException e) {
			log.info("expired token >> {}", e.getMessage());
			throw new AuthException(EXPIRED_TOKEN);
		} catch (JwtException e) {
			log.info("invalid token >> {}", e.getMessage());
			throw new AuthException(INVALID_TOKEN);
		} catch (IllegalArgumentException e) {
			log.info("wrong token >> {}", e.getMessage());
			throw new AuthException(WRONG_TOKEN);
		}
	}

	public String extractEmail(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.get("email", String.class);
	}

	private static Map<String, Object> createClaims(String email, String type) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(TYPE_CLAIM_KEY, type);
		claims.put(EMAIL_CLAIM_KEY, email);
		return claims;
	}
}
