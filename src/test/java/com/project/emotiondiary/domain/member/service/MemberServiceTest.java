package com.project.emotiondiary.domain.member.service;

import static com.project.emotiondiary.global.error.type.MemberErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.emotiondiary.domain.member.entity.Gender;
import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.entity.Role;
import com.project.emotiondiary.domain.member.model.LoginRequest;
import com.project.emotiondiary.domain.member.model.LoginResponse;
import com.project.emotiondiary.domain.member.model.ReissueResponse;
import com.project.emotiondiary.domain.member.model.SignUpRequest;
import com.project.emotiondiary.domain.member.model.SignUpResponse;
import com.project.emotiondiary.domain.member.model.UpdatePasswordRequest;
import com.project.emotiondiary.domain.member.model.WithDrawRequest;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.global.auth.model.InvalidatedAccessToken;
import com.project.emotiondiary.global.auth.model.RefreshToken;
import com.project.emotiondiary.global.auth.repository.InvalidatedAccessTokenRepository;
import com.project.emotiondiary.global.auth.repository.RefreshTokenRepository;
import com.project.emotiondiary.global.auth.service.JwtProvider;
import com.project.emotiondiary.global.error.exception.MemberException;
import com.project.emotiondiary.helper.IntegrationTestSupport;

class MemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberService memberService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired
	private InvalidatedAccessTokenRepository invalidatedAccessTokenRepository;

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

	@DisplayName("비밀번호와 비밀번호 확인이 일치하지 않으면 예외를 던진다.")
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
		Member member = createMemberWithEncodedPassword();
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
		Member member = createMemberWithEncodedPassword();
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

		Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(response.getRefreshToken());
		assertThat(refreshToken.isPresent()).isTrue();
	}

	@DisplayName("redis에 저장된 refresh token이 없으면 예외를 던진다.")
	@Test
	void reissueWithNoRefreshTokenInRedis() {
		// given
		String refreshToken = jwtProvider.generateRefreshToken("test@test.com");

		// when & then
		assertThatThrownBy(() -> memberService.reissueToken(refreshToken))
			.isInstanceOf(MemberException.class)
			.hasMessage(REISSUE_TOKEN_FAILED.getMessage());
	}

	@DisplayName("redis에 저장된 refresh token이 유효하지 않으면 예외를 던진다.")
	@Test
	void reissueWithInvalidRefreshToken() {
		// given
		String refreshToken = "hello1234";
		RefreshToken token = RefreshToken.builder()
			.id(refreshToken)
			.email("test@test.com")
			.expiration(1800000L)
			.build();

		refreshTokenRepository.save(token);

		// when & then
		assertThatThrownBy(() -> memberService.reissueToken(refreshToken))
			.isInstanceOf(MemberException.class)
			.hasMessage(REISSUE_TOKEN_FAILED.getMessage());
	}

	@DisplayName("refresh token에서 추출한 이메일을 가진 회원이 존재하지 않으면 예외를 던진다.")
	@Test
	void reissueWithNotFoundMember() {
		// given
		String refreshToken = jwtProvider.generateRefreshToken("test@test.com");

		RefreshToken token = RefreshToken.builder()
			.id(refreshToken)
			.email("test@test.com")
			.expiration(1800000L)
			.build();

		refreshTokenRepository.save(token);

		// when & then
		assertThatThrownBy(() -> memberService.reissueToken(refreshToken))
			.isInstanceOf(MemberException.class)
			.hasMessage(MEMBER_NOT_FOUND.getMessage());
	}

	@DisplayName("refresh token 통해 access token 발급받는다.")
	@Test
	void reissue() {
		// given
		Member member = createMember();

		memberRepository.save(member);

		String refreshToken = jwtProvider.generateRefreshToken("test@test.com");

		RefreshToken token = RefreshToken.builder()
			.id(refreshToken)
			.email("test@test.com")
			.expiration(1800000L)
			.build();

		refreshTokenRepository.save(token);

		// when
		ReissueResponse response = memberService.reissueToken(refreshToken);

		// then
		assertThat(response.getAccessToken()).isNotNull();

		String emailInToken = jwtProvider.extractEmail(response.getAccessToken());
		String roleInToken = jwtProvider.extractRole(response.getAccessToken());
		assertThat(emailInToken).isEqualTo("test@test.com");
		assertThat(roleInToken).isEqualTo(Role.ROLE_USER.name());
	}

	@DisplayName("회원 로그아웃에 성공한다.")
	@Test
	void logout() {
		// given
		Member member = createMember();

		memberRepository.save(member);

		String accessToken = jwtProvider.generateAccessToken("test@test.com", Role.ROLE_USER);
		String refreshToken = jwtProvider.generateRefreshToken("test@test.com");

		RefreshToken token = RefreshToken.builder()
			.id(refreshToken)
			.email("test@test.com")
			.expiration(1800000L)
			.build();

		refreshTokenRepository.save(token);

		// when
		Map<String, String> response = memberService.logout(member, String.format("Bearer %s", accessToken));

		// then
		Optional<RefreshToken> optional1 = refreshTokenRepository.findById(refreshToken);
		assertThat(optional1.isPresent()).isFalse();

		Optional<InvalidatedAccessToken> optional2 = invalidatedAccessTokenRepository.findById(accessToken);
		assertThat(optional2.isPresent()).isTrue();
		assertThat(optional2.get())
			.extracting("email", "token")
			.contains("test@test.com", accessToken);
		assertThat(response)
			.extracting("message")
			.isEqualTo("로그아웃 성공");
	}

	@DisplayName("입력 받은 비밀번호가 틀리면 경우 예외를 던진다.")
	@Test
	void withdrawWithWrongPassword() {
		// given
		Member member = createMemberWithEncodedPassword();
		memberRepository.save(member);

		WithDrawRequest request = new WithDrawRequest("qwe123");

		// when & then
		assertThatThrownBy(() -> memberService.withdraw(member, request))
			.isInstanceOf(MemberException.class)
			.hasMessage(WITHDRAW_FAILED.getMessage());
	}

	@DisplayName("회원 탈퇴에 성공한다.")
	@Test
	void withdraw() {
		// given
		Member member = createMemberWithEncodedPassword();
		memberRepository.save(member);

		WithDrawRequest request = new WithDrawRequest("zxc123");

		// when
		Map<String, String> response = memberService.withdraw(member, request);

		// then
		assertThat(response)
			.extracting("message")
			.isEqualTo("회원 탈퇴가 성공적으로 처리되었습니다.");

	}

	@DisplayName("회원의 기존 비밀번호가 틀리면 예외를 던진다.")
	@Test
	void updatePasswordWithWrongPassword() {
		// given
		Member member = createMemberWithEncodedPassword();
		memberRepository.save(member);

		UpdatePasswordRequest request = UpdatePasswordRequest.builder()
			.password("qwe123")
			.newPassword("asd123")
			.newPasswordCheck("asd123")
			.build();

		// when & then
		assertThatThrownBy(() -> memberService.updatePassword(member, request))
			.isInstanceOf(MemberException.class)
			.hasMessage(UPDATE_PASSWORD_FAILED.getMessage());
	}
	@DisplayName("새로운 비밀번호와 비밀번호 확인이 일치하지 않으면 예외를 던진다.")
	@Test
	void updatePasswordWithMismatchPasswordCheck() {
		// given
		Member member = createMemberWithEncodedPassword();
		memberRepository.save(member);

		UpdatePasswordRequest request = UpdatePasswordRequest.builder()
			.password("zxc123")
			.newPassword("asd123")
			.newPasswordCheck("qwe123")
			.build();

		// when & then
		assertThatThrownBy(() -> memberService.updatePassword(member, request))
			.isInstanceOf(MemberException.class)
			.hasMessage(MISMATCH_PASSWORD_CHECK.getMessage());
	}

	@DisplayName("회원 비밀번호 변경에 성공한다.")
	@Test
	void updatePassword() {
		// given
		Member member = createMemberWithEncodedPassword();
		memberRepository.save(member);

		UpdatePasswordRequest request = UpdatePasswordRequest.builder()
			.password("zxc123")
			.newPassword("asd123")
			.newPasswordCheck("asd123")
			.build();

		// when
		Map<String, String> response = memberService.updatePassword(member, request);

		// then
		assertThat(response)
			.extracting("message")
			.isEqualTo("비밀번호가 성공적으로 변경되었습니다.");
	}

	private static Member createMember() {
		return Member.builder()
			.email("test@test.com")
			.role(Role.ROLE_USER)
			.build();
	}

	private Member createMemberWithEncodedPassword() {
		return Member.builder()
			.email("test@test.com")
			.password(passwordEncoder.encode("zxc123"))
			.build();
	}
}