package com.project.emotiondiary.domain.member.service;

import static com.project.emotiondiary.global.error.type.MemberErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.emotiondiary.domain.member.entity.Gender;
import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.model.LoginRequest;
import com.project.emotiondiary.domain.member.model.LoginResponse;
import com.project.emotiondiary.domain.member.model.SignUpRequest;
import com.project.emotiondiary.domain.member.model.SignUpResponse;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.global.error.exception.MemberException;
import com.project.emotiondiary.helper.IntegrationTestSupport;

class MemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberService memberService;

	@Autowired
	private PasswordEncoder passwordEncoder;


	@DisplayName("입력 받은 이메일이 이미 존재하는 경우 예외를 던진다.")
	@Test
	void checkEmailWithDuplicatedEmail() {
		// given
		Member member = Member.builder()
			.email("test@test.com")
			.nickname("hello")
			.gender(Gender.MALE)
			.build();
		memberRepository.save(member);

		String email = "test@test.com";

		// when & then
		assertThatThrownBy(() -> memberService.checkEmailDuplicated(email))
			.isInstanceOf(MemberException.class)
			.hasMessage(ALREADY_EXIST_EMAIL.getMessage());
	}

	@DisplayName("입력 받은 이메일이 존재하지 않는 경우 사용 가능하다.")
	@Test
	void checkEmail() {
		// given
		String email = "test@test.com";
		// when
		Map<String, String> response = memberService.checkEmailDuplicated(email);
		// then
		assertThat(response.get("message")).isEqualTo("사용 가능한 이메일입니다.");
	}

	@DisplayName("입력 받은 닉네임이 이미 존재하는 경우 예외를 던진다.")
	@Test
	void checkEmailWithDuplicatedNickname() {
		// given
		Member member = Member.builder()
			.email("test@test.com")
			.nickname("hello")
			.gender(Gender.MALE)
			.build();
		memberRepository.save(member);

		String nickname = "hello";

		// when & then
		assertThatThrownBy(() -> memberService.checkNicknameDuplicated(nickname))
			.isInstanceOf(MemberException.class)
			.hasMessage(ALREADY_EXIST_NICKNAME.getMessage());
	}

	@DisplayName("입력 받은 닉네임이 존재하지 않는 경우 사용 가능하다.")
	@Test
	void checkNickname() {
		// given
		String nickname = "hello";
		// when
		Map<String, String> response = memberService.checkNicknameDuplicated(nickname);
		// then
		assertThat(response.get("message")).isEqualTo("사용 가능한 닉네임입니다.");
	}

	@DisplayName("회원 가입 시 입력 받은 두 비밀번호가 일치하지 않으면 예외를 던진다.")
	@Test
	void signUpWithMismatchPasswordCheck() {
		// given
		SignUpRequest request = SignUpRequest.builder()
			.email("test@test.com")
			.nickname("hello")
			.password("zxc123")
			.passwordCheck("qwe123")
			.checkEmail(true)
			.checkNick(true)
			.gender(Gender.MALE)
			.build();
		// when & then
		assertThatThrownBy(() -> memberService.signUp(request))
			.isInstanceOf(MemberException.class)
			.hasMessage(MISMATCH_PASSWORD_CHECK.getMessage());
	}

	@DisplayName("회원 가입에 성공한다.")
	@Test
	void signUp() {
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
		// when
		SignUpResponse response = memberService.signUp(request);
		// then
		assertThat(response)
			.extracting("nickname")
			.isEqualTo("hello");
	}

	@DisplayName("존재하지 않는 회원이 로그인을 하면 예외를 던진다.")
	@Test
	void loginWithNotFoundMember() {
		// given
		LoginRequest request = LoginRequest.builder()
			.email("test@test.com")
			.password("zxc123")
			.build();
		// when & then
		assertThatThrownBy(() -> memberService.login(request))
			.isInstanceOf(MemberException.class)
			.hasMessage(MEMBER_NOT_FOUND.getMessage());
	}

	@DisplayName("잘못된 비밀번호로 로그인을 하면 예외를 던진다.")
	@Test
	void loginWithWrongPassword() {
		// given
		Member member = Member.builder()
			.email("test@test.com")
			.password(passwordEncoder.encode("zxc123"))
			.build();
		memberRepository.save(member);

		LoginRequest request = LoginRequest.builder()
			.email("test@test.com")
			.password("qwe123")
			.build();
		// when & then
		assertThatThrownBy(() -> memberService.login(request))
			.isInstanceOf(MemberException.class)
			.hasMessage(FAIL_TO_LOGIN.getMessage());
	}

	@DisplayName("회원 로그인에 성공한다.")
	@Test
	void login() {
		// given
		Member member = Member.builder()
			.email("test@test.com")
			.password(passwordEncoder.encode("zxc123"))
			.build();
		memberRepository.save(member);

		LoginRequest request = LoginRequest.builder()
			.email("test@test.com")
			.password("zxc123")
			.build();
		// when
		LoginResponse response = memberService.login(request);
		// then
		assertThat(response.getAccessToken()).isNotNull();
		assertThat(response.getRefreshToken()).isNotNull();
	}
}