package com.project.emotiondiary.global.auth.interceptor;

import static com.project.emotiondiary.global.error.type.AuthErrorCode.*;

import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.project.emotiondiary.global.auth.annotation.AuthRequired;
import com.project.emotiondiary.global.auth.repository.InvalidatedAccessTokenRepository;
import com.project.emotiondiary.global.auth.service.CustomUserDetailsService;
import com.project.emotiondiary.global.auth.service.JwtProvider;
import com.project.emotiondiary.global.error.exception.AuthException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtProvider jwtProvider;
	private final CustomUserDetailsService userDetailsService;
	private final InvalidatedAccessTokenRepository invalidatedAccessTokenRepository;

	// 요청이 controller 도달하기 전에 실행
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		if (handler instanceof HandlerMethod handlerMethod) {
			// 어노테이션이 메서드에 적용되어 있는지 확인
			if (handlerMethod.getMethod().isAnnotationPresent(AuthRequired.class)) {
				log.info("resolve access token from header");
				String token = resolveAccessToken(request);

				try {
					if (!Objects.isNull(token) && jwtProvider.validateToken(token)) {
						log.info("access token is valid");
						invalidatedAccessTokenRepository.findById(token).ifPresent(
							invalidated -> {
								log.info("access token is invalidated by logout");
								throw new AuthException(AUTHENTICATION_FAILED);
							}
						);
						// 토큰에서 사용자 이메일 추출
						String email = jwtProvider.extractEmail(token);

						UserDetails userDetails = userDetailsService.loadUserByUsername(email);
						Authentication authentication
							= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

						log.info("save authentication object in SecurityContext");
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				} catch (AuthException e) {
					throw new AuthException(AUTHENTICATION_FAILED);
				}
			}
		}
		return true;
	}

	private String resolveAccessToken(HttpServletRequest request) {
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
			return authHeader.substring(BEARER_PREFIX.length());
		}
		return null;
	}
}
