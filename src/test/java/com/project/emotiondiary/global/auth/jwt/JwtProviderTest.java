package com.project.emotiondiary.global.auth.jwt;

import static com.project.emotiondiary.global.error.type.AuthErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.project.emotiondiary.domain.member.entity.Role;
import com.project.emotiondiary.global.error.exception.AuthException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@ActiveProfiles("test")
class JwtProviderTest {

	@Value("${spring.jwt.secret}")
	private String secretKey;

	@Autowired
	private JwtProvider jwtProvider;


	@DisplayName("claims 포함한 access token을 정상적으로 발행한다.")
	@Test
	void generateAccessToken() {
		// given
		String email = "test@test.com";
		Role role = Role.ROLE_USER;

		// when
		String token = jwtProvider.generateAccessToken(email, role);

		// then
		assertThat(token).isNotNull();

		Claims claims = getClaims(token);
		assertThat(email).isEqualTo(claims.get("email", String.class));
		assertThat("access").isEqualTo(claims.get("type", String.class));
		assertThat(role.name()).isEqualTo(claims.get("role", String.class));
		assertDoesNotThrow(claims::getExpiration);
	}

	@DisplayName("claims 포함한 refresh token을 정상적으로 발행한다.")
	@Test
	void generateRefreshToken() {
		// given
		String email = "test@test.com";

		// when
		String token = jwtProvider.generateRefreshToken(email);

		// then
		Claims claims = getClaims(token);
		assertThat(email).isEqualTo(claims.get("email", String.class));
		assertThat("refresh").isEqualTo(claims.get("type", String.class));
		assertDoesNotThrow(claims::getExpiration);
	}

	@DisplayName("정상적인 토큰의 유효성을 검사한다.")
	@Test
	void validateToken() {
		// given
		String token = createToken(1800000L);

		// when
		boolean result = jwtProvider.validateToken(token);

		// then
		assertThat(result).isTrue();
	}

	@DisplayName("만료된 토큰의 유효성을 검사한다.")
	@Test
	void validateTokenWithExpired() {
		// given
		String token = createToken(0L);

		// when & then
		assertThatThrownBy(() -> jwtProvider.validateToken(token))
			.isInstanceOf(AuthException.class)
			.hasMessage(EXPIRED_TOKEN.getMessage());
	}

	@DisplayName("유효하지 않은 토큰의 유효성을 검사한다.")
	@Test
	void validateTokenWithInvalid() {
		// given
		String token = "a1b2c3de5";

		// when & then
		assertThatThrownBy(() -> jwtProvider.validateToken(token))
			.isInstanceOf(AuthException.class)
			.hasMessage(INVALID_TOKEN.getMessage());
	}

	@DisplayName("잘못된 토큰의 유효성을 검증한다.")
	@Test
	void validateTokenWithWrong() {
		// given
		String token = ""; // 빈 문자열

		// when & then
		assertThatThrownBy(() -> jwtProvider.validateToken(token))
			.isInstanceOf(AuthException.class)
			.hasMessage(WRONG_TOKEN.getMessage());
	}

	@DisplayName("토큰에서 이메일 정보를 추출한다.")
	@Test
	void extractEmail() {
		// given
		String token = createToken(1800000L);

		// when
		String email = jwtProvider.extractEmail(token);

		// then
		assertThat(email).isEqualTo("test@test.com");
	}

	private String createToken(Long tokenValid) {
		Date now = new Date();
		Map<String, Object> claims = new HashMap<>();
		claims.put("email", "test@test.com");
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + tokenValid))
			.signWith(
				Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
				SignatureAlgorithm.HS256
			)
			.compact();
	}

	private Claims getClaims(String accessToken) {
		return assertDoesNotThrow(() ->
			Jwts.parserBuilder()
				.setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
				.build()
				.parseClaimsJws(accessToken)
				.getBody());
	}
}