package com.project.emotiondiary.domain.member.service;

import static com.project.emotiondiary.global.error.type.MemberErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.project.emotiondiary.domain.member.entity.Gender;
import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.global.error.exception.MemberException;

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberService memberService;

	@AfterEach
	void tearDown() {
		memberRepository.deleteAllInBatch();
	}

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
}