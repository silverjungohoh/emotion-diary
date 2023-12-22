package com.project.emotiondiary.docs;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emotiondiary.global.auth.jwt.JwtAuthInterceptor;

@Import(RestDocsConfig.class)
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTestSupport {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected WebApplicationContext context;

	@Autowired
	protected RestDocumentationResultHandler restDocs;

	@MockBean
	protected JwtAuthInterceptor jwtAuthInterceptor;

	@BeforeEach
	void setUp(RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(provider))
			.alwaysDo(MockMvcResultHandlers.print())
			.alwaysDo(restDocs)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@BeforeEach
	void setUp() {
		given(jwtAuthInterceptor.preHandle(any(), any(), any())).willReturn(true);

	}
}
