package com.project.emotiondiary.helper;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emotiondiary.docs.RestDocsTestSupport;
import com.project.emotiondiary.domain.diary.controller.DiaryController;
import com.project.emotiondiary.domain.diary.service.DiaryService;
import com.project.emotiondiary.domain.member.controller.MemberController;
import com.project.emotiondiary.domain.member.service.MemberService;
import com.project.emotiondiary.global.auth.jwt.JwtAuthInterceptor;
import com.project.emotiondiary.global.config.SecurityConfig;

@WebMvcTest(
	controllers = {MemberController.class, DiaryController.class},
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)}
)
public class ControllerTestSupport extends RestDocsTestSupport {

	protected final String AUTHORIZATION = "Authorization";
	protected final String BEARER_PREFIX = "Bearer %s";
	protected final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6Ikp";

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected MemberService memberService;

	@MockBean
	protected DiaryService diaryService;

	@MockBean
	protected JwtAuthInterceptor jwtAuthInterceptor;

	@BeforeEach
	void setUp() {
		given(jwtAuthInterceptor.preHandle(any(), any(), any())).willReturn(true);
	}
}
