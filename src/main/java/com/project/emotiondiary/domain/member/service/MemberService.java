package com.project.emotiondiary.domain.member.service;

import static com.project.emotiondiary.global.error.type.MemberErrorCode.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.model.SignUpRequest;
import com.project.emotiondiary.domain.member.model.SignUpResponse;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.global.error.exception.MemberException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public Map<String, String> checkEmailDuplicated(String email) {

		if (memberRepository.existsByEmail(email)) {
			throw new MemberException(ALREADY_EXIST_EMAIL);
		}

		return getMessage("사용 가능한 이메일입니다.");
	}

	@Transactional(readOnly = true)
	public Map<String, String> checkNicknameDuplicated(String nickname) {

		if(memberRepository.existsByNickname(nickname)) {
			throw new MemberException(ALREADY_EXIST_NICKNAME);
		}

		return getMessage("사용 가능한 닉네임입니다.");
	}

	@Transactional
	public SignUpResponse signUp(SignUpRequest request) {

		if(!Objects.equals(request.getPassword(), request.getPasswordCheck())) {
			throw new MemberException(MISMATCH_PASSWORD_CHECK);
		}

		Member member = Member.builder()
			.email(request.getEmail())
			.password(request.getPassword())
			.nickname(request.getNickname())
			.gender(request.getGender())
			.build();

		memberRepository.save(member);
		return SignUpResponse.from(member);
	}

	private static Map<String, String> getMessage(String message) {
		Map<String, String> result = new HashMap<>();
		result.put("message", message);
		return result;
	}
}
