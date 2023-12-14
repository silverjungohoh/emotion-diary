package com.project.emotiondiary.domain.member.controller;

import static com.project.emotiondiary.global.error.type.MemberErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emotiondiary.domain.member.service.MemberService;
import com.project.emotiondiary.global.error.exception.MemberException;

@WebMvcTest
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private MemberService memberService;

	@DisplayName("입력 받은 이메일이 이미 존재하는 경우 예외를 던진다.")
	@Test
	void checkEmailWithDuplicatedEmail() throws Exception {
		// given
		String email = "test@test.com";
		given(memberService.checkEmailDuplicated(anyString())).willThrow(new MemberException(ALREADY_EXIST_EMAIL));
		// when & then
		mockMvc.perform(get("/api/members/check/email").param("email", email))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(ALREADY_EXIST_EMAIL.getCode()))
			.andExpect(jsonPath("$.status").value(ALREADY_EXIST_EMAIL.getStatus()))
			.andExpect(jsonPath("$.message").value(ALREADY_EXIST_EMAIL.getMessage()))
			.andDo(print());
	}

	@DisplayName("입력 받은 이메일이 존재하지 않는 경우 사용 가능하다.")
	@Test
	void checkEmail() throws Exception {
		// given
		String email = "test@test.com";

		Map<String, String> response = new HashMap<>();
		response.put("message", "사용 가능한 이메일입니다.");
		given(memberService.checkEmailDuplicated(anyString())).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/members/check/email").param("email", email))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("사용 가능한 이메일입니다."))
			.andDo(print());
	}

	@DisplayName("입력 받은 닉네임이 이미 존재하는 경우 예외를 던진다.")
	@Test
	void checkNicknameWithDuplicatedNickname() throws Exception {
		// given
		String nickname = "hello";
		given(memberService.checkNicknameDuplicated(anyString())).willThrow(
			new MemberException(ALREADY_EXIST_NICKNAME));
		// when & then
		mockMvc.perform(get("/api/members/check/nickname").param("nickname", nickname))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(ALREADY_EXIST_NICKNAME.getCode()))
			.andExpect(jsonPath("$.status").value(ALREADY_EXIST_NICKNAME.getStatus()))
			.andExpect(jsonPath("$.message").value(ALREADY_EXIST_NICKNAME.getMessage()))
			.andDo(print());
	}

	@DisplayName("입력 받은 닉네임이 존재하지 않는 경우 사용 가능하다.")
	@Test
	void checkNickname() throws Exception {
		// given
		String nickname = "hello";

		Map<String, String> response = new HashMap<>();
		response.put("message", "사용 가능한 닉네임입니다.");
		given(memberService.checkNicknameDuplicated(anyString())).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/members/check/nickname").param("nickname", nickname))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("사용 가능한 닉네임입니다."))
			.andDo(print());
	}

}