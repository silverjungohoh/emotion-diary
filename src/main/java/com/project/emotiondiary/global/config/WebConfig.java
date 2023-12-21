package com.project.emotiondiary.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.project.emotiondiary.global.auth.jwt.JwtAuthInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final JwtAuthInterceptor jwtAuthInterceptor;

	// 인터셉터 등록
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(jwtAuthInterceptor);
	}
}
