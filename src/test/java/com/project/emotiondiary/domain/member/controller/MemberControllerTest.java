package com.project.emotiondiary.domain.member.controller;

import static com.project.emotiondiary.global.error.type.CommonErrorCode.*;
import static com.project.emotiondiary.global.error.type.MemberErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import com.project.emotiondiary.docs.RestDocsTestSupport;
import com.project.emotiondiary.domain.member.entity.Gender;
import com.project.emotiondiary.domain.member.model.LoginRequest;
import com.project.emotiondiary.domain.member.model.LoginResponse;
import com.project.emotiondiary.domain.member.model.SignUpRequest;
import com.project.emotiondiary.domain.member.model.SignUpResponse;
import com.project.emotiondiary.domain.member.service.MemberService;
import com.project.emotiondiary.global.error.exception.MemberException;

@WebMvcTest
class MemberControllerTest extends RestDocsTestSupport {

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
			.andDo(
				restDocs.document(
					queryParameters(
						parameterWithName("email").description("이메일")
					),
					responseFields(
						fieldWithPath("status").description("HTTP 상태 코드"),
						fieldWithPath("message").description("에러 메세지"),
						fieldWithPath("code").description("에러 코드"),
						fieldWithPath("fieldErrors").description("유효성 검증 실패에 대한 정보")
					)
				)
			);
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
			.andDo(
				restDocs.document(
					queryParameters(
						parameterWithName("email").description("이메일")
					),
					responseFields(
						fieldWithPath("message").description("응답 메세지")
					)
				)
			);
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
			.andDo(
				restDocs.document(
					queryParameters(
						parameterWithName("nickname").description("닉네임")
					),
					responseFields(
						fieldWithPath("status").description("HTTP 상태 코드"),
						fieldWithPath("message").description("에러 메세지"),
						fieldWithPath("code").description("에러 코드"),
						fieldWithPath("fieldErrors").description("유효성 검증 실패에 대한 정보")
					)
				)
			);
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
			.andDo(
				restDocs.document(
					queryParameters(
						parameterWithName("nickname").description("닉네임")
					),
					responseFields(
						fieldWithPath("message").description("응답 메세지")
					)
				)
			);
	}

	@DisplayName("회원 가입 시 입력 받은 두 비밀번호가 일치하지 않으면 예외를 던진다.")
	@Test
	void signUpWithMismatchPasswordCheck() throws Exception {
		// given
		SignUpRequest request = SignUpRequest.builder()
			.email("test@test.com")
			.nickname("hello")
			.password("zxc123")
			.passwordCheck("zxc123")
			.checkEmail(true)
			.checkNick(true)
			.gender(Gender.MALE)
			.build();

		given(memberService.signUp(any())).willThrow(new MemberException(MISMATCH_PASSWORD_CHECK));

		// when & then
		mockMvc.perform(post("/api/members/sign-up")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(MISMATCH_PASSWORD_CHECK.getCode()))
			.andExpect(jsonPath("$.status").value(MISMATCH_PASSWORD_CHECK.getStatus()))
			.andExpect(jsonPath("$.message").value(MISMATCH_PASSWORD_CHECK.getMessage()))
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("email").description("이메일"),
						fieldWithPath("password").description("비밀번호"),
						fieldWithPath("passwordCheck").description("비밀번호 확인"),
						fieldWithPath("nickname").description("닉네임"),
						fieldWithPath("gender").description("성별"),
						fieldWithPath("isCheckEmail").description("이메일 중복확인 여부"),
						fieldWithPath("isCheckNick").description("닉네임 중복확인 여부")
					),
					responseFields(
						fieldWithPath("status").description("HTTP 상태 코드"),
						fieldWithPath("message").description("에러 메세지"),
						fieldWithPath("code").description("에러 코드"),
						fieldWithPath("fieldErrors").description("유효성 검증 실패에 대한 정보")
					)
				)
			);
	}

	@DisplayName("회원 가입에 성공한다.")
	@Test
	void signUp() throws Exception {
		// given
		SignUpRequest request = SignUpRequest.builder()
			.email("test@test.com")
			.nickname("hello")
			.password("zxc123")
			.passwordCheck("zxc123")
			.checkEmail(true)
			.checkNick(true)
			.gender(Gender.MALE)
			.build();

		SignUpResponse response = new SignUpResponse("hello");
		given(memberService.signUp(any())).willReturn(response);

		// when & then
		mockMvc.perform(post("/api/members/sign-up")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nickname").value(request.getNickname()))
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("email").description("이메일"),
						fieldWithPath("password").description("비밀번호"),
						fieldWithPath("passwordCheck").description("비밀번호 확인"),
						fieldWithPath("nickname").description("닉네임"),
						fieldWithPath("gender").description("성별"),
						fieldWithPath("isCheckEmail").description("이메일 중복확인 여부"),
						fieldWithPath("isCheckNick").description("닉네임 중복확인 여부")
					),
					responseFields(
						fieldWithPath("nickname").description("가입한 회원의 닉네임")
					)
				)
			);
	}

	@DisplayName("회원 가입 시 유효성 검증에 실패한 경우 예외를 던진다.")
	@Test
	void signUpWithInvalid() throws Exception {
		// given
		SignUpRequest request = SignUpRequest.builder()
			.email("test@test.com")
			.nickname("")
			.password("zxc123")
			.passwordCheck("zxc123")
			.checkEmail(false)
			.checkNick(false)
			.gender(Gender.MALE)
			.build();

		// when & then
		mockMvc.perform(post("/api/members/sign-up")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INPUT_VALUE_INVALID.getCode()))
			.andExpect(jsonPath("$.status").value(INPUT_VALUE_INVALID.getStatus()))
			.andExpect(jsonPath("$.message").value(INPUT_VALUE_INVALID.getMessage()))
			.andExpect(jsonPath("$.fieldErrors.size()").value(3))
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("email").description("이메일"),
						fieldWithPath("password").description("비밀번호"),
						fieldWithPath("passwordCheck").description("비밀번호 확인"),
						fieldWithPath("nickname").description("닉네임"),
						fieldWithPath("gender").description("성별"),
						fieldWithPath("isCheckEmail").description("이메일 중복확인 여부"),
						fieldWithPath("isCheckNick").description("닉네임 중복확인 여부")
					),
					responseFields(
						fieldWithPath("status").description("HTTP 상태 코드"),
						fieldWithPath("message").description("에러 메세지"),
						fieldWithPath("code").description("에러 코드"),
						fieldWithPath("fieldErrors").description("유효성 검증 실패에 대한 정보"),
						fieldWithPath("fieldErrors[].field").description("유효성 검증 실패한 필드 이름"),
						fieldWithPath("fieldErrors[].reason").description("유효성 검증 실패한 필드의 메세지")
					)
				)
			);
	}

	@DisplayName("존재하지 않는 회원이 로그인을 하면 예외를 던진다.")
	@Test
	void loginWithNotFoundMember() throws Exception {
		// given
		LoginRequest request = LoginRequest.builder()
			.email("test@test.com")
			.password("zxc123")
			.build();

		given(memberService.login(any())).willThrow(new MemberException(MEMBER_NOT_FOUND));

		// when & then
		mockMvc.perform(post("/api/members/login")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.status").value(MEMBER_NOT_FOUND.getStatus()))
			.andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("email").description("회원 이메일"),
						fieldWithPath("password").description("회원 비밀번호")
					),
					responseFields(
						fieldWithPath("status").description("HTTP 상태 코드"),
						fieldWithPath("message").description("에러 메세지"),
						fieldWithPath("code").description("에러 코드"),
						fieldWithPath("fieldErrors").description("유효성 검증 실패에 대한 정보")
					)
				)
			);
	}

	@DisplayName("잘못된 비밀번호로 로그인을 하면 예외를 던진다.")
	@Test
	void loginWithWrongPassword() throws Exception {
		// given
		LoginRequest request = LoginRequest.builder()
			.email("test@test.com")
			.password("zxc123")
			.build();

		given(memberService.login(any())).willThrow(new MemberException(FAIL_TO_LOGIN));

		// when & then
		mockMvc.perform(post("/api/members/login")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value(FAIL_TO_LOGIN.getCode()))
			.andExpect(jsonPath("$.status").value(FAIL_TO_LOGIN.getStatus()))
			.andExpect(jsonPath("$.message").value(FAIL_TO_LOGIN.getMessage()))
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("email").description("회원 이메일"),
						fieldWithPath("password").description("회원 비밀번호")
					),
					responseFields(
						fieldWithPath("status").description("HTTP 상태 코드"),
						fieldWithPath("message").description("에러 메세지"),
						fieldWithPath("code").description("에러 코드"),
						fieldWithPath("fieldErrors").description("유효성 검증 실패에 대한 정보")
					)
				)
			);
	}

	@DisplayName("회원 로그인에 성공한다.")
	@Test
	void login() throws Exception {
		// given
		LoginRequest request = LoginRequest.builder()
			.email("test@test.com")
			.password("zxc123")
			.build();

		LoginResponse response = LoginResponse.builder()
			.accessToken(
				"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
					+ ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
					+ ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
			.refreshToken(
				"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
					+ ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ"
					+ ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
			.build();

		given(memberService.login(any())).willReturn(response);

		// when & then
		mockMvc.perform(post("/api/members/login")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").value(response.getAccessToken()))
			.andExpect(jsonPath("$.refreshToken").value(response.getRefreshToken()))
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("email").description("회원 이메일"),
						fieldWithPath("password").description("회원 비밀번호")
					),
					responseFields(
						fieldWithPath("accessToken").description("회원 access token"),
						fieldWithPath("refreshToken").description("회원 refresh token")
					)
				)
			);
	}
}